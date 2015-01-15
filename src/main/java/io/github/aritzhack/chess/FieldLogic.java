package io.github.aritzhack.chess;

import com.google.common.collect.Sets;

import java.awt.Point;
import java.util.Set;

import static io.github.aritzhack.chess.Piece.PieceType.*;

/**
 * @author Aritz Lopez
 */
public class FieldLogic {

    private final int width = 8, height = 8;
    private final Piece[][] pieces = new Piece[width][height];
    private boolean blacksTurn = false;
    private boolean whiteKingChecked, blackKingChecked;

    public void setPiece(int x, int y, Piece.PieceType piece, boolean isBlack) {
        if (inBounds(x, y)) this.pieces[x][y] = new Piece(piece, isBlack);
    }

    public void setBlacksTurn(boolean blacksTurn) {
        this.blacksTurn = blacksTurn;
    }

    public void calculateMovements() {
        calculateMovements(false);
    }

    private void calculateMovements(boolean checked) {
        boolean noneChecked = true;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Set<Point> points = Sets.newHashSet();
                Piece piece = this.pieces[x][y];
                if (piece.isBlack() != blacksTurn) continue;
                A:
                for (Point[] ps : piece.getPossibleMovements(x, y)) {
                    for (Point p : ps) {
                        if (!inBounds(p.x, p.y)) continue A;

                        if (piece.getType() == PAWN && Math.abs(p.y - y) == 1 && isPiece(p.x, p.y)) {
                            continue A;
                        }
                        if (!isPiece(p.x, p.y) || isBlack(p.x, p.y) != isBlack(x, y)) points.add(p);
                        if (isPiece(p.x, p.y)) continue A;
                    }
                }
                if (piece.getType() == PAWN) {
                    int d = piece.isBlack() ? +1 : -1;
                    if (inBounds(x - 1, y + d) && isPiece(x - 1, y + d) && isBlack(x - 1, y + d) != piece.isBlack()) {
                        points.add(new Point(x - 1, y + d));
                    }
                    if (inBounds(x + 1, y + d) && isPiece(x + 1, y + d) && isBlack(x + 1, y + d) != piece.isBlack()) {
                        points.add(new Point(x + 1, y + d));
                    }
                }
                for (Point p : points) {
                    if (this.pieces[p.x][p.y].getType() == KING) {
                        noneChecked = false;
                    }
                }
                piece.setMovements(points);
            }
        }
        whiteKingChecked = !noneChecked && blacksTurn;
        blackKingChecked = !noneChecked && !blacksTurn;
    }

    public boolean isBlackKingChecked() {
        return blackKingChecked;
    }

    public boolean isWhiteKingChecked() {
        return whiteKingChecked;
    }

    private boolean isPiece(int x, int y) {
        return inBounds(x, y) && this.pieces[x][y].getType() != NONE;
    }

    private boolean isBlack(int x, int y) {
        return isPiece(x, y) && this.pieces[x][y].isBlack();
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
