
/*
CS 1027B – Assignment 4
Name: Tanya Sahota
Student Number: 251446953
Email: tsahot@uwo.ca
Created: April 2, 2025
*/

import java.util.Iterator;

public class DirectoryTree {
	
	// Initializing private instance variable
    private FileSystemObject root;

    // Initialize the constructor DirectoryTree with a specific root object.
    public DirectoryTree(FileSystemObject r) {
        this.root = r;
    }

    // Return the root.
    public FileSystemObject getRoot() {
        return root;
    }

    // Determine and return the level of the given file/folder, fso, in the tree (the root is at level 0).
    public int level(FileSystemObject fso) {
        int level = 0;
        FileSystemObject current = fso;
        
        // While current is not null or the root, increment the level & move up the parent chain until you reach the root.
        while (current != root && current != null) {
            level++;
            current = current.getParent();
        }
        return level; // Return the level of fso.
    }

    // Determine and return the lowest common ancestor of nodes a and b in the tree.
    public FileSystemObject lca(FileSystemObject a, FileSystemObject b) {
    	// Adjust levels so that both objects are at the same level within the tree.
        int levelA = level(a);
        int levelB = level(b);
        if (levelA < levelB) {
            b = getAncestorAtLevel(b, levelA);
        } else if (levelB < levelA) {
            a = getAncestorAtLevel(a, levelB);
        }
        
        // Recursively find common ancestor.
        return lcaHelper(a, b);
    }

    // Recursive helper method
    private FileSystemObject lcaHelper(FileSystemObject a, FileSystemObject b) {
        if (a == b) return a; // Return common ancestor.
        if (a == null || b == null) return null; // Return null if one is missing.
        return lcaHelper(a.getParent(), b.getParent());
    }

    // Initialize a private helper method to move a node upward until its level matches the target level.
    private FileSystemObject getAncestorAtLevel(FileSystemObject node, int targetLevel) {
        while (level(node) > targetLevel) {
            node = node.getParent();
        }
        return node;
    }

    // Determine and return the complete filepath from node a to node b in the tree.
    public String buildPath(FileSystemObject a, FileSystemObject b) {
        if (a == b) {
        	// Return the name of b as the path if both object are the same.
            return b.getName();
        }

        // Find the lca between a & b.
        FileSystemObject ancestor = lca(a, b);
        
        // Handle same parent case for a & b.
        if (a.getParent() == b.getParent()) {
            return b.getName();
        }

        StringBuilder path = new StringBuilder();
        
        // Build upward path.
        FileSystemObject current = a;
        while (current != ancestor) {
            path.append("../");
            current = current.getParent();
        }

        // Build downward path.
        if (ancestor != b) {
            path.append(getDownPath(ancestor, b));
        }

        return path.toString();
    }

    // Initialize private helper method for buildPath to generate the downward part.
    private String getDownPath(FileSystemObject from, FileSystemObject to) {
        if (from == to) {
            return "";
        }
        
        // Return to's name if 'to' is a direct child of 'from'.
        if (to.getParent() == from) {
            return to.getName();
        }
        
        // Otherwise, find intermediate parent.
        FileSystemObject temp = to;
        while (temp.getParent() != from) {
            temp = temp.getParent();
        }
        
        // Recursively build path.
        return temp.getName() + "/" + getDownPath(temp, to);
    }

    // Return a string representing the entire directory tree.
    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildTreeString(root, 0, sb);
        // If trailing newline exists, remove it.
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    // Initialize private helper method to build string representation of the tree.
    private void buildTreeString(FileSystemObject node, int level, StringBuilder sb) {
    	
    	// Add 2 spaces to create indentation for each level below the root.
        for (int i = 1; i < level; i++) {
            sb.append("  ");
        }
        
        // Add "-" before the name for any non-root node.
        if (level > 0) {
            sb.append(" - ");
        }
        
        // Add a newline to the object's name.
        sb.append(node.getName()).append("\n");
        
        // If the node is not a file, get it's children recursively.
        if (!node.isFile() && node.getChildren() != null) {
            Iterator<FileSystemObject> it = node.getChildren().iterator();
            while (it.hasNext()) {
                buildTreeString(it.next(), level + 1, sb);
            }
        }
    }

    public void cutPaste(FileSystemObject f, FileSystemObject dest) throws DirectoryTreeException {
    	
        // If dest is a file (not a folder), throw a DirectoryTreeException to say that we cannot store files/folders inside a file.
        if (dest.isFile()) {
            throw new DirectoryTreeException("Cannot paste into a file");
        }
        
        // If f is the root of the entire DirectoryTree, throw a DirectoryTreeException to say that we cannot remove/cut/move the root of the whole tree.
        if (f == root) {
            throw new DirectoryTreeException("Cannot move root");
        }
        
        // Take the file/folder structure that is rooted at f and remove it from its current place in the tree and move it into the dest folder.
        FileSystemObject oldParent = f.getParent();
        oldParent.getChildren().remove(f);
        dest.addChild(f);
    }

    public void copyPaste(FileSystemObject f, FileSystemObject dest) throws DirectoryTreeException {
    	
    	//If dest is a file (not a folder), throw a DirectoryTreeException to say that we cannot store files/folders inside a file.
        if (dest.isFile()) {
            throw new DirectoryTreeException("Cannot paste into a file");
        }
        
        //Take the file/folder structure that is rooted at f and make a clone of it (do not remove it) so that the clone is rooted in the dest folder.
        FileSystemObject copy = copyRecursive(f);
        dest.addChild(copy);
    }

    // Recursive helper method to clone a FileSystemObject.
    private FileSystemObject copyRecursive(FileSystemObject original) {
        FileSystemObject copy;
        
        // Create a new ComputerFile with the same name & size, id + 100 if the original is a file.
        if (original.isFile()) {
            ComputerFile originalFile = (ComputerFile) original;
            copy = new ComputerFile(originalFile.getName(), originalFile.getID() + 100, originalFile.size());
        } else {
        	
        	// Otherwise, create a new FileSystemObject with id increased if it is a folder.
            copy = new FileSystemObject(original.getName(), original.getID() + 100);
            
            // Loop through and copy all the children.
            if (original.getChildren() != null) {
                Iterator<FileSystemObject> it = original.getChildren().iterator();
                while (it.hasNext()) {
                    FileSystemObject childCopy = copyRecursive(it.next());
                    copy.addChild(childCopy);
                }
            }
        }
        
        // Return the cloned object.
        return copy;
    }
}

