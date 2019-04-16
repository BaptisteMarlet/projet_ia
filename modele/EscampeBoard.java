package modele;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EscampeBoard {

	int lisereActuel = -1;

	boolean joueurActuel; // true si noir, false si blanc
	private boolean BlancGagne = false;
	private boolean NoirGagne = false;
	private Timer timer = new Timer();
	Case[][] array;
	public int secondsPassed = 0;
	public HashSet<Piece> pieces;
	private boolean partieNull = false;


	public EscampeBoard() {

		array = new Case[6][6];
		array[0][0] = new Case(1,this);
		array[1][0] = new Case(2,this);
		array[2][0] = new Case(2,this);
		array[3][0] = new Case(3,this);
		array[4][0] = new Case(1,this);
		array[5][0] = new Case(2,this);

		array[0][1] = new Case(3,this);
		array[1][1] = new Case(1,this);
		array[2][1] = new Case(3,this);
		array[3][1] = new Case(1,this);
		array[4][1] = new Case(3,this);
		array[5][1] = new Case(2,this);

		array[0][2] = new Case(2,this);
		array[1][2] = new Case(3,this);
		array[2][2] = new Case(1,this);
		array[3][2] = new Case(2,this);
		array[4][2] = new Case(1,this);
		array[5][2] = new Case(3,this);

		array[0][3] = new Case(2,this);
		array[1][3] = new Case(1,this);
		array[2][3] = new Case(3,this);
		array[3][3] = new Case(2,this);
		array[4][3] = new Case(3,this);
		array[5][3] = new Case(1,this);

		array[0][4] = new Case(1,this);
		array[1][4] = new Case(3,this);
		array[2][4] = new Case(1,this);
		array[3][4] = new Case(3,this);
		array[4][4] = new Case(1,this);
		array[5][4] = new Case(2,this);

		array[0][5] = new Case(3,this);
		array[1][5] = new Case(2,this);
		array[2][5] = new Case(2,this);
		array[3][5] = new Case(1,this);
		array[4][5] = new Case(3,this);
		array[5][5] = new Case(2,this);

		pieces = new HashSet<>();
	}

	public boolean isValidMove(String move, String player) {
		return isValidMoveBool(move, player == "noir");
	}

	private boolean isValidMoveBool(String move, boolean player) {
		Coup c = new Coup(move);
		if (array[c.fromX][c.fromY].getPiece() != null && array[c.fromX][c.fromY].getPiece().player == player) {
			if (array[c.toX][c.toY].getPiece() == null) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Vérifie que le joueur du booléen peut passer par la case (x,y)
	 */
	public boolean traversable(int x, int y, boolean player) {
		if ((x < 6 && x >= 0) && (y < 6 && y >= 0) && (array[x][y].getPiece() == null)) {
			return true;
		}
		return false;
	}

	/*
	 * Retourne sous forme de coups les adresses des cases qui sont autour de la
	 * case x, y
	 */
	public Coup[] horizon1(int x, int y, boolean player) {
		Coup[] returned = new Coup[4];
		if (traversable(x, y - 1, player)) {
			returned[3] = new Coup(x, y, x, y - 1);
		}
		if (traversable(x - 1, y, player)) {
			returned[2] = new Coup(x, y, x - 1, y);
		}
		if (traversable(x, y + 1, player)) {
			returned[1] = new Coup(x, y, x, y + 1);
		}
		if (traversable(x + 1, y, player)) {
			returned[0] = new Coup(x, y, x + 1, y);
		}
		return returned;
	}

	public String[] possibleMoves(String player) {
		boolean p = (player == "noir");
		ArrayList<String> listOfMoves = new ArrayList<>();

		for (Piece piece : pieces) {
			if (piece.player == p) {
				for (Coup c : piece.possibleMoves()) {
					try {
						listOfMoves.add(c.toString());
					} catch (NullPointerException e) {
						// les coups nulls sont des coups impossibles.
					}
				}
			}
		}

		String[] returned = new String[listOfMoves.size()];
		return listOfMoves.toArray(returned);
	}

	public void play(String move, String player) {
		if (move.length() == 5) {
			Coup c = new Coup(move);
			if(this.array[c.fromX][c.fromY].piece.licorne && player == "noir") {
				this.NoirGagne = true;
			}
			if(this.array[c.fromX][c.fromY].piece.licorne && player == "blanc") {
				this.BlancGagne = true;
			}
			if(this.secondsPassed==500) {
				this.partieNull=true;
			}
			this.array[c.fromX][c.fromY].bougerPiece(c);
			this.lisereActuel = this.array[c.toX][c.toY].lisere;
		} else {
			Coup.debutPartie(move, this, (player == "noir"));
		}

	}

	public boolean mettrePiece(Piece p) {
		if (this.traversable(p.getX(), p.getY(), p.player)) {
			this.pieces.add(p);
			return array[p.getX()][p.getY()].mettrePiece(p);
		}
		return false;
	}

	public String toString() {
		String texte = "";
		for (int i = 0; i < 6; i++) {

			for (int j = 0; j < 6; j++) {
				if (this.array[j][i].getPiece() != null && this.array[j][i].getPiece().licorne
						&& this.array[j][i].getPiece().player) {
					texte = texte + "N";
				}
				if (this.array[j][i].getPiece() != null && this.array[j][i].getPiece().licorne
						&& !this.array[j][i].getPiece().player) {
					texte = texte + "B";
				}

				if (this.array[j][i].getPiece() != null && !this.array[j][i].getPiece().licorne
						&& this.array[j][i].getPiece().player) {
					texte = texte + "n";
				}
				if (this.array[j][i].getPiece() != null && !this.array[j][i].getPiece().licorne
						&& !this.array[j][i].getPiece().player) {
					texte = texte + "b";
				}
				if (this.array[j][i].getPiece() == null) {
					texte = texte + "-";
				}
				texte += this.array[j][i].getLisere();
				texte += " ";

			}
			texte = texte + "\n";
		}
		return texte;
	}
	//
	//Initialise un fichier de sauvegarde depuis un board
	//
	public void saveToFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		String texte = "%  ABCDEF"+"\n";
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if(j==0) {
					texte = texte+"0"+(i+1)+" ";
				}
				if (this.array[j][i].getPiece() != null && this.array[j][i].getPiece().licorne
						&& this.array[j][i].getPiece().player) {
					texte = texte + "N";
				}
				if (this.array[j][i].getPiece() != null && this.array[j][i].getPiece().licorne
						&& !this.array[j][i].getPiece().player) {
					texte = texte + "B";
				}

				if (this.array[j][i].getPiece() != null && !this.array[j][i].getPiece().licorne
						&& this.array[j][i].getPiece().player) {
					texte = texte + "n";
				}
				if (this.array[j][i].getPiece() != null && !this.array[j][i].getPiece().licorne
						&& !this.array[j][i].getPiece().player) {
					texte = texte + "b";
				}
				if (this.array[j][i].getPiece() == null) {
					texte = texte + "-";
				}
				if(j==5) {
					texte = texte+" 0"+(i+1);
				}
			}
			texte = texte + "\n";
		}
		texte = texte + "%  ABCDEF";
		Files.write(path, texte.getBytes());
	}

	//
	//Initialise un plateau depuis un fichier sauvegarde
	//
	public void setFromFile(String fileName) throws IOException {

		File file = new File(fileName);
		int numeroLigne = 0;
		int numeroColonne = 0;
		ArrayList<Character> tableauDeSauvegarde = new ArrayList<Character>();

		try {
			List<String> lignes = Files.readAllLines(Paths.get(fileName));
			for (String ligne:lignes) {
				if(ligne.charAt(0)!='%') {
					for(int i=0;i<ligne.length();i++){
						tableauDeSauvegarde.add(ligne.charAt(i));
					}
				}
			}
			System.out.println(tableauDeSauvegarde);
			for (char c : tableauDeSauvegarde) {
				if (c == 'N') {
					System.out.println(" numero de la ligne : " + numeroLigne + " numero de la colonne " + numeroColonne);

					Piece p = new Piece(true, true, this);
					this.array[numeroColonne][numeroLigne].mettrePiece(p);
					numeroColonne = (numeroColonne + 1) % 6;
					if (numeroColonne == 0) {
						numeroLigne = numeroLigne + 1;
					}
				}
				if (c == 'n') {
					Piece p = new Piece(true, false, this);
					this.array[numeroColonne][numeroLigne].mettrePiece(p);
					numeroColonne = (numeroColonne + 1) % 6;
					if (numeroColonne == 0) {
						numeroLigne = numeroLigne + 1;
					}
				}
				if (c == 'B') {
					Piece p = new Piece(false, true, this);
					this.array[numeroColonne][numeroLigne].mettrePiece(p);
					numeroColonne = (numeroColonne + 1) % 6;
					if (numeroColonne == 0) {
						numeroLigne = numeroLigne + 1;
					}
				}
				if (c == 'b') {
					Piece p = new Piece(false, false, this);
					this.array[numeroColonne][numeroLigne].mettrePiece(p);
					numeroColonne = (numeroColonne + 1) % 6;
					if (numeroColonne == 0) {
						numeroLigne = numeroLigne + 1;
					}
				}
				if (c == '-') {
					System.out.println(" numero de la ligne : " + numeroLigne + " numero de la colonne " + numeroColonne);

					this.array[numeroColonne][numeroLigne].piece = null;
					numeroColonne = (numeroColonne + 1) % 6;
					if (numeroColonne == 0) {
						numeroLigne = numeroLigne + 1;
					}

				}


			}
		}
		catch (IOException e) {
			System.err.println("impossible d'ouvrir le fichier " + file.toString());
		}
	}

	TimerTask task = new TimerTask() {
		public void run() {
			secondsPassed++;
			System.out.println("Seconds passed: "+ secondsPassed);
		}
	};
	public void startTimer() {
		this.timer.scheduleAtFixedRate(task, 1000, 1000);
	}

	public boolean gameOver() {
		//TODO a faire
		if(this.partieNull) {
			return true;

		}
		if(this.BlancGagne) {
			return true;
		}
		if(this.NoirGagne) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws IOException {

		EscampeBoard b = new EscampeBoard();
		b.startTimer();

		b.setFromFile("Test");
		b.saveToFile("test.txt");


	}
}
