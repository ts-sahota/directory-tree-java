
/*
CS 1027B – Assignment 4
Name: Tanya Sahota
Student Number: 251446953
Email: tsahot@uwo.ca
Created: March 31, 2025
*/

import java.util.Iterator;

public class FileSystemObject implements Comparable<FileSystemObject> {
	// Private instance variables
    private String name;
    private OrderedListADT<FileSystemObject> children; // List of files/folders inside (if folder)
    private FileSystemObject parent; // Parent object in the directory tree
    private int id; // Identifier for the object

    // Initializes the constructor FileSystemObject with the specific name and id
    // If this is a folder (not a file), initialize the children object.
    public FileSystemObject(String name, int id) {
        this.name = name;
        this.id = id;
        this.parent = null;
        if (!isFile()) {
            this.children = new ArrayOrderedList<>();
        } else {
            this.children = null;
        }
    }

    // Gets and returns the name of the object.
    public String getName() {
        return name;
    }
    
    // Gets and returns the ID of the object.
    public int getID() {
        return id;
    }

    // Gets and returns the parent of the object in the directory tree.
    public FileSystemObject getParent() {
        return parent;
    }

    // Gets and returns the list of children.
    public OrderedListADT<FileSystemObject> getChildren() {
        return children;
    }

    // Sets the name of this object.
    public void setName(String name) {
        this.name = name;
    }

    // Sets the parent of this object.
    public void setParent(FileSystemObject parent) {
        this.parent = parent;
    }

    // Determine whether this represents a file (as opposed to a folder) by checking if this file is an instance of ComputerFile. 
    // Return true if it is a file or false otherwise.
    public boolean isFile() {
        if (this instanceof ComputerFile) { 
            return true; 
        } else {
            return false; 
        }
    }

    // Adds a child to this folder
    public void addChild(FileSystemObject node) throws DirectoryTreeException {
    	// If this a file, throw a DirectoryTreeException to say that we cannot store a file/folder within a file.
        if (isFile()) {
            throw new DirectoryTreeException("Cannot add child to a file");
        }
        // If this already has a file with the same name stored within it (as a child), throw a DirectoryTreeException to say that we cannot have two files/folders with the same name stored in the same folder.
        if (hasChildWithName(node.getName())) {
            throw new DirectoryTreeException("Duplicate name: " + node.getName());
        }
        // Set this object as the parent of the new node & add it to he children list.
        node.setParent(this);
        children.add(node);
    }

    // Checks if the children list already contains an object with a given name.
    private boolean hasChildWithName(String name) {
        if (children == null) return false;
        Iterator<FileSystemObject> it = children.iterator();
        while (it.hasNext()) {
            FileSystemObject child = it.next();
            if (child.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // Return the name of the file or folder in string representation.
    public String toString() {
        return name;
    }

    // Determine and return the size of this object.
    public int size() {
        if (isFile()) { // If this is a file, return its size.
            return 0; // In the base class, return 0 for files since the actual file size is provided by ComputerFile.
        }
        return calculateTreeSize(this);
    }

    // If this is a folder, recursively compute the size of all its contents (not just immediate children but all files and folders that are ancestors of this folder).
    private int calculateTreeSize(FileSystemObject node) {
        if (node.isFile()) {
            return ((ComputerFile)node).size(); // When the node is a file, it returns the file size by casting to ComputerFile.
        }
        
        int totalSize = 0;
        if (node.children != null) {
            Iterator<FileSystemObject> it = node.children.iterator();
            while (it.hasNext()) {
                totalSize += calculateTreeSize(it.next());
            }
        }
        return totalSize;
    }
    
    // Returns an appropriate int value to indicate which FileSystemObject is larger between this and other.
    public int compareTo(FileSystemObject other) {
    	// Folders should always come before files (i.e. they are smaller than files)
        if (!this.isFile() && other.isFile()) {
            return -1;
        }
        if (this.isFile() && !other.isFile()) {
            return 1;
        }
        // If both are folders OR both are files, use alphabetical, case-insensitive ordering.
        return this.name.compareToIgnoreCase(other.name);
    }
}