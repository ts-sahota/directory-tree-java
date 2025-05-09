
/*
CS 1027B – Assignment 4
Name: Tanya Sahota
Student Number: 251446953
Email: tsahot@uwo.ca
Created: April 2, 2025
*/

public class ComputerFile extends FileSystemObject {
	
	// Holds the size of the file
    private int size;

    // Initializes the constructor with the specified name, id, and size.
    public ComputerFile(String name, int id, int size) {
    	
    	// Initialize the FileSystemObject with the corresponding parameters and initialize the size instance variable
        super(name, id);
        this.size = size;
    }

    // Returns the file size.
    public int size() {
        return size;
    }
}
