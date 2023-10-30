package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {
            if (!Files.isDirectory(path)) {
                throw new UnsupportedOperationException("LIST command can only be used on directories.");
            } else {
                try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(path)) {
                    System.out.println("Contents of " + path);
                    for (Path p : pathStream) {
                        System.out.println(p.getFileName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return path;
        }
    },

    SHOW() {
        private FileReader fileReader = new FileReader();
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) {
            try {
                String newPathString = path + File.separator + parameters[1];
                Path newPath = Paths.get(newPathString);
                if (Files.isDirectory(newPath)) {
                    throw new UnsupportedOperationException("SHOW command cannot be used on directories.");
                } else {
                    String fileName = newPath.getFileName().toString();
                    if (fileName.endsWith(".mp3") || fileName.endsWith(".mp4")) {
                        throw new UnsupportedOperationException("Extension not supported.");
                    } else {
                        fileReader.read(newPath);
                    }
                    return path;
                }
            } catch (IndexOutOfBoundsException e){
                throw new UnsupportedOperationException("Please specify what to show");
            }
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {
            if (path.equals(Paths.get(Application.ROOT))) {
                throw new UnsupportedOperationException("Cannot go beyond the root directory.");
            } else {
                return path.getParent();
            }
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {
            try {
                String newDirectory = parameters[1];
                path = Paths.get(path + File.separator + newDirectory);
                if (Files.isDirectory(path)) {
                    return path;
                } else {
                    throw new UnsupportedOperationException("OPEN command can only be used on directories.");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new UnsupportedOperationException("Please specify the directory to be opened");
            }
        }
    },

    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) {
            try {
                String file = parameters[1];
                Path filePath = Paths.get(path + File.separator + file);
                BasicFileAttributeView basicView = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
                BasicFileAttributes basicAttributes = basicView.readAttributes();

                System.out.println("Is directory " + "[" + basicAttributes.isDirectory() + "]");
                System.out.println("Size " + "[" + basicAttributes.size() + "]");
                System.out.println("Created on " + "[" + basicAttributes.creationTime() + "]");
                System.out.println("Last access time " + "[" + basicAttributes.lastAccessTime() + "]");

                return path;
            } catch (IndexOutOfBoundsException | IOException e) {
                throw new UnsupportedOperationException("Please specify a valid file or directory to be detailed");
            }
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Saindo...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }

        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}
