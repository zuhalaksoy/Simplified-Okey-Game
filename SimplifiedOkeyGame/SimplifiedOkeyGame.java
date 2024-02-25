import java.util.Random;

public class SimplifiedOkeyGame {

    Player[] players;
    Tile[] tiles;
    int tileCount;
    Tile lastDiscardedTile;
    private boolean devMode;

    int currentPlayerIndex = 0;

    public SimplifiedOkeyGame() {
        players = new Player[4];
    }

    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // four copies of each value, no jokers
        for (int i = 1; i <= 26; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[currentTile++] = new Tile(i);
            }
        }

        tileCount = 104;
    }

    /*
     * distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles, this method assumes the tiles are already shuffled
     */
    public void distributeTilesToPlayers() {

        for(int i = 0; i < 15; i++){
            players[0].addTile(tiles[0]);  
            tileCount--;
            removeTopTile(tiles);
        }

        for(int j = 1; j < 4; j++){
            for(int i = 0; i < 14; i++){
                players[j].addTile(tiles[0]); 
                tileCount--;
                removeTopTile(tiles);
            }
        }
    }

    //removing the top tile from the tiles array 
    public static void removeTopTile(Tile[] arr) {
        if (arr.length == 0) {
            return; 
        }

        for (int i = 0; i < arr.length - 1; i++) {
            arr[i] = arr[i + 1]; 
        }
        arr[arr.length - 1] = null;
    }

    /*
     * get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {
        players[getCurrentPlayerIndex()].addTile(lastDiscardedTile); 
        return lastDiscardedTile.toString();
    }

    /*
     * get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * and it will be given to the current player
     * returns the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        players[getCurrentPlayerIndex()].addTile(tiles[0]);

        //the top tile of the tiles array
        String s = tiles[0].toString();

        removeTopTile(tiles);
        tileCount--;

        return s;
    }

    /*
     * should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {

        Random rand = new Random();

        for (int i = 0; i < tiles.length; i++) {
			int randomIndexToSwap = i + rand.nextInt(tiles.length - i);
			Tile temp = tiles[randomIndexToSwap];
			tiles[randomIndexToSwap] = tiles[i];
			tiles[i] = temp;
		}

    }

    /*
     * check if game still continues, should return true if current player
     * finished the game. use checkWinning method of the player class to determine
     */
    public boolean didGameFinish() {
        return players[currentPlayerIndex].checkWinning();
    }

    /* finds the player who has the highest number for the longest chain
     * if multiple players have the same length may return multiple players
     */
    public Player[] getPlayerWithHighestLongestChain() {

        int numberOfWinners = 0;
        int maxChain = players [0].findLongestChain();

        //Evaluates the longest chain
        for (int i = 1; i < players.length; i++) {
            if (players[i + 1].findLongestChain() > maxChain) {
                maxChain = players[i + 1].findLongestChain();
            }
        }

        //Determines number of winners
        for (int i = 0; i < players.length; i++) {
            if (players[i].findLongestChain() == maxChain) {
                numberOfWinners++;
            }
        }

        Player[] winners = new Player[numberOfWinners];

        //Add winners to the array
        for (int i = 0; i < players.length; i++) {
            int j = 0;
            if (players[i].findLongestChain() == maxChain) {
                winners [j] = players [i];
                j++;
            }
        }

        return winners;
    }
    
    /*
     * checks if there are more tiles on the stack to continue the game
     */
    public boolean hasMoreTileInStack() {
        return tileCount != 0;
    }

    /*
     * pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * you should check if getting the discarded tile is useful for the computer
     * by checking if it increases the longest chain length, if not get the top tile
     */
    public void pickTileForComputer() {
        //test if it is worth getting the discarded card
        players[currentPlayerIndex].addTile(lastDiscardedTile);
        int newChain = players[currentPlayerIndex].findLongestChain();
        players[currentPlayerIndex].getAndRemoveTile(players[currentPlayerIndex].findPositionOfTile(lastDiscardedTile));

        if (players[currentPlayerIndex].findLongestChain() < newChain) {
            getLastDiscardedTile();
            if(devMode)
                System.out.println(getCurrentPlayerName() + " picked up from discard.");
            
            
        }
        else{
            getTopTile();
            if(devMode)
               System.out.println(getCurrentPlayerName() + " picked up from tiles.");
            
            
        }
    }

    /*
     * Current player will discard a duplicate tile.
     * If there are no duplicates return false.
     */
    public boolean discardDuplicate() {
        boolean hasDuplicate = false;
        int discardIndex = -1;
        int index = 0;

        while (!hasDuplicate && index <= players[currentPlayerIndex].playerTiles.length - 2) {
            if (players[currentPlayerIndex].playerTiles[index].getValue() == players[currentPlayerIndex].playerTiles[index + 1].getValue()) {
                discardIndex = index;
                hasDuplicate = true;
            }
            index++;
        }

        if (hasDuplicate) {
            discardTile(discardIndex);
        }

        return hasDuplicate;
    }

    /*
     * Current computer player will discard the least useful tile.
     * you may choose based on how useful each tile is
     */
    public void discardTileForComputer() {
        if (!discardDuplicate()) {
            //discard the farthest away tile from the longest chain
            double middleIndex = players[currentPlayerIndex].findMiddleOfLongestChain();
            if (middleIndex == players[currentPlayerIndex].playerTiles.length / 2) {
                if (Math.random() < 0.5) {
                    discardTile(0);
                }
                else{
                    discardTile(players[currentPlayerIndex].playerTiles.length - 1);
                }
            }
            else if (middleIndex < players[currentPlayerIndex].playerTiles.length / 2) {
                discardTile(players[currentPlayerIndex].playerTiles.length - 1);
            }
            else{
                discardTile(0);
            }
        }
    }

    /*
     * discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {

        Player currentPlayer = players[getCurrentPlayerIndex()];
        lastDiscardedTile = currentPlayer.getAndRemoveTile(tileIndex);

    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }
}
