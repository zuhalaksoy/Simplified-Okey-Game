public class Player {
    String playerName;
    Tile[] playerTiles;
    int numberOfTiles;

    public Player(String name) {
        setName(name);
        playerTiles = new Tile[15]; // there are at most 15 tiles a player owns at any time
        numberOfTiles = 0; // currently this player owns 0 tiles, will pick tiles at the beggining of the game
    }
    /*
     * checks this player's hand to determine if this player is winning
     * the player with a complete chain of 14 consecutive numbers wins the game
     * note that the player whose turn is now draws one extra tile to have 15 tiles in hand,
     * and the extra tile does not disturb the longest chain and therefore the winning condition
     * check the assigment text for more details on winning condition
     */
    public boolean checkWinning() {
        int maxLength = findLongestChain();
        return maxLength >= 14;   
    }
    /*
     * used for finding the longest chain in this player hand
     * this method should iterate over playerTiles to find the longest chain
     * of consecutive numbers, used for checking the winning condition
     * and also for determining the winner if tile stack has no tiles
     */
    public int findLongestChain() {
        int longestChain = 0;
        int currentChain = 0;
        for (int i = 0; i < playerTiles.length - 2; i++)
        {
            
            if (playerTiles[i].getValue() == playerTiles[i + 1].getValue() + 1) {
                currentChain++; 
                if (currentChain > longestChain) {
                    longestChain = currentChain; 
                }
            }
            else if (playerTiles[i].getValue() != playerTiles[i + 1].getValue()) {
                currentChain = 1; // Reset the current chain length
            }
        }
        return longestChain;
    }

    /*
     * Used for determining the middle index of the longest chain.
     * If there are more than one longest chains find the middle of the middle indexes.
     */
    public double findMiddleOfLongestChain() {
        int longestChain = 0;
        int currentChain = 0;
        double middleIndex = 0;
        for (int i = 0; i < playerTiles.length - 2; i++)
        {
            
            if (playerTiles[i].getValue() == playerTiles[i + 1].getValue() + 1) {
                currentChain++; 
                if (currentChain > longestChain) {
                    longestChain = currentChain; 
                    middleIndex = longestChain / 2.0 + i;
                }
                else if (currentChain == longestChain) {// if there are two longest chains
                    middleIndex = middleIndex + longestChain / 2.0 + i;
                }
            }
            else if (playerTiles[i].getValue() != playerTiles[i + 1].getValue()) {
                currentChain = 1; // Reset the current chain length
            }
        }
        return middleIndex;
    }

    /*
     * removes and returns the tile in given index position
     */
    public Tile getAndRemoveTile(int index) {
        if ( this.numberOfTiles > index ) {
            Tile result = this.playerTiles[index];
            for ( int i = index + 1; i < this.numberOfTiles; i++ ) {
                this.playerTiles[i - 1] = this.playerTiles[i];
            }
            numberOfTiles--;
            return result;
        }
        return null;
    }
    /*
     * adds the given tile to this player's hand keeping the ascending order
     * this requires you to loop over the existing tiles to find the correct position,
     * then shift the remaining tiles to the right by one
     */
    public void addTile(Tile t) {
        if ( this.numberOfTiles < 15 ) {
             // Find the correct position to insert the tile
            int insertIndex = 0;
            while (insertIndex < numberOfTiles && t.getValue() > playerTiles[insertIndex].getValue()) {
                insertIndex++;
            }
            // Shifting tiles to one space right to make a space for the new tile.
            for (int i = numberOfTiles - 1; i >= insertIndex; i--) {
                playerTiles[i + 1] = playerTiles[i];
            }
            // Insert the tile at the correct position
            playerTiles[insertIndex] = t;
            numberOfTiles++;
        }
        else {
            System.out.println( "You can't have more than 15 tiles.");
        }
    }

    /*
     * finds the index for a given tile in this player's hand
     */
    public int findPositionOfTile(Tile t) {
        int tilePosition = -1;
        for (int i = 0; i < numberOfTiles; i++) {
            if(playerTiles[i].matchingTiles(t)) {
                tilePosition = i;
            }
        }
        return tilePosition;
    }

    /*
     * displays the tiles of this player
     */
    public void displayTiles() {
        System.out.println(playerName + "'s Tiles:");
        for (int i = 0; i < numberOfTiles; i++) {
            System.out.print(playerTiles[i].toString() + " ");
        }
        System.out.println();
    }

    public Tile[] getTiles() {
        return playerTiles;
    }

    public void setName(String name) {
        playerName = name;
    }

    public String getName() {
        return playerName;
    }
}
