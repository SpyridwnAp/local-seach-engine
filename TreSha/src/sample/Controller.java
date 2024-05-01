package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private String comboboxSelectedItem;

    private String[] listFile = new String[20];
    private LuceneTester tester = new LuceneTester();
    private ListView<String> resultslist = new ListView<>();
    int selectednum = 0;
    private String hitsString = "";

    @FXML
    private BorderPane bp;

    @FXML
    private TextField search;

    @FXML
    private ComboBox<?> cb;

    @FXML
    void searchAction(ActionEvent event) {
        comboboxSelectedItem = (String) cb.getSelectionModel().getSelectedItem();
        //System.out.println(helper);
        tester.setCbselect(comboboxSelectedItem);
        System.out.println(search.getText());
        try {
            tester.search(search.getText());

            setResultstoList();
            if (!resultslist.getItems().isEmpty()) {
                Stage stage = (Stage) bp.getScene().getWindow();
                Dialog<BorderPane> dialog = new Dialog<>();
                dialog.initOwner(stage);
                BorderPane bp3 = new BorderPane();
                Label resultlabel = new Label("Results");
                Label hitslabel = new Label(hitsString);
                VBox resultvbox = new VBox();
                Button openFile = new Button("Open file");
                openFile.setAlignment(Pos.CENTER);
                resultvbox.getChildren().addAll(resultlabel, hitslabel, resultslist, openFile);
                bp3.setCenter(resultvbox);
                dialog.getDialogPane().setContent(bp3);

                openFile.setOnAction(e -> {
                    String currentLine = "";
                    String allcont = "";
                    String body = "";
                    String people = "";
                    String title = "";
                    String places = "";
                    String fileToOpenPath = resultslist.getSelectionModel().getSelectedItem();
                    System.out.println(">>>>>>>>>" + fileToOpenPath);
                    File fileToOpen = new File(fileToOpenPath);
                    System.out.println(fileToOpen.getPath() + "..................................................");
                    try {
                        BufferedReader brOpenFile = new BufferedReader(new FileReader(fileToOpen));
                        while ((currentLine = brOpenFile.readLine()) != null) {
                            //System.out.println (currentLine);
                            allcont = allcont + " " + currentLine + "\n";
                        }
                        if (allcont.contains("<BODY>")) {
                            int pFrom = allcont.indexOf("<BODY>") + "<BODY>".length();
                            int pTo = allcont.lastIndexOf("</BODY>");
                            body = allcont.substring(pFrom, pTo);
                            //System.out.println (body);
                        }
                        if (allcont.contains("<PEOPLE>")) {
                            int pFrom = allcont.indexOf("<PEOPLE>") + "<PEOPLE>".length();
                            int pTo = allcont.lastIndexOf("</PEOPLE>");
                            people = allcont.substring(pFrom, pTo);
                            // System.out.println (people);
                        }
                        if (allcont.contains("<TITLE>")) {
                            int pFrom = allcont.indexOf("<TITLE>") + "<TITLE>".length();
                            int pTo = allcont.lastIndexOf("</TITLE>");
                            title = allcont.substring(pFrom, pTo);
                            // System.out.println (title);
                        }
                        if (allcont.contains("<PLACES>")) {
                            int pFrom = allcont.indexOf("<PLACES>") + "<PLACES>".length();
                            int pTo = allcont.lastIndexOf("</PLACES>");
                            places = allcont.substring(pFrom, pTo);
                            // System.out.println (places);
                        }
                        Dialog<BorderPane> dialogOpen = new Dialog<>();
                        dialogOpen.initOwner(bp.getScene().getWindow());
                        Label placesLabel = new Label(places);
                        Label peopleLabel = new Label(people);
                        Label titleLabel = new Label(title);
                        Label bodyLabel = new Label(body);
                        VBox vbOpenFile = new VBox();
                        vbOpenFile.setAlignment(Pos.CENTER);
                        vbOpenFile.getChildren().addAll(placesLabel, peopleLabel, titleLabel, bodyLabel);
                        BorderPane bp4 = new BorderPane();
                        bp4.setCenter(vbOpenFile);
                        dialogOpen.getDialogPane().setContent(bp4);
                        dialogOpen.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                        dialogOpen.setTitle(fileToOpen.getName());
                        dialogOpen.setResizable(true);
                        dialogOpen.getDialogPane().setPrefSize(480, 320);
                        dialogOpen.showAndWait();

                        brOpenFile.close();

                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }catch (IOException ioException){
                        ioException.printStackTrace();
                    }
                });

                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                dialog.setTitle("Results");
                dialog.setResizable(true);
                dialog.getDialogPane().setPrefSize(480, 320);
                dialog.showAndWait();
                resultslist.getItems().clear();
            } else {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("NO RESULTS");
                a.setContentText("No results found!");
                a.showAndWait();
            }
        } catch (ParseException | IOException e) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("ERROR");
            a.setContentText("Your Field is empty or your query is wrong!Try again!");
            a.showAndWait();
        }

    }


    @FXML
    void addanddelete(ActionEvent event) {
        Stage stage = (Stage) bp.getScene().getWindow();
        Dialog<BorderPane> dialogAddDeleteEdit = new Dialog<>();
        dialogAddDeleteEdit.initOwner(stage);
        BorderPane bp2 = new BorderPane();
        ListView<File> AddDeleteListview = new ListView<>();
        Button selectfilebutton = new Button("Select File");
        Button addbutton = new Button("ADD");
        Button deletebutton = new Button("DELETE");


        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setMargin(selectfilebutton, new Insets(5, 5, 5, 5));
        vb.setMargin(AddDeleteListview, new Insets(5, 5, 5, 5));

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.setMargin(addbutton, new Insets(5, 5, 5, 5));
        hb.setMargin(deletebutton, new Insets(5, 5, 5, 5));

        hb.getChildren().addAll(addbutton, deletebutton);
        vb.getChildren().addAll(selectfilebutton, AddDeleteListview, hb);
        bp2.setCenter(vb);
        dialogAddDeleteEdit.getDialogPane().setContent(bp2);

        selectfilebutton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                if (AddDeleteListview.getItems().contains(selectedFile)) {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("ERROR");
                    a.setContentText("File already selected!");
                    a.showAndWait();
                } else {
                    AddDeleteListview.getItems().addAll(selectedFile);
                    AddDeleteListview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    //System.out.println("epilegmeno arxeioo:" + selectedFile);
                }
            } else if (selectedFile == null) {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("ERROR");
                a.setContentText("File is not valid!");
                a.showAndWait();
            }
        });

        addbutton.setOnAction(e -> {
            String filename = " ";
            List<File> selectedItems = AddDeleteListview.getSelectionModel().getSelectedItems();
            System.out.println(selectedItems);
            if (!AddDeleteListview.getSelectionModel().getSelectedItems().isEmpty()) {
                for (File filePath : selectedItems) {
                    try {
                        Files.move(Paths.get(filePath.getPath()), Paths.get("D:\\IntllijProjectFolder\\FirstBrowser\\Reuters_articles\\" + filePath.getName()));
                        filename = filename + " " + filePath.getName();
                    } catch (IOException ex) {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setTitle("ERROR");
                        a.setContentText("Failed to add the file!");
                        a.showAndWait();
                    }
                }
                AddDeleteListview.getItems().removeAll(selectedItems);
                try {
                    tester.createIndex();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("SUCCESS");
                a.setContentText("File(s) " + filename + " added successfully!");
                a.showAndWait();
            } else {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("ERROR");
                a.setContentText("No selected file");
                a.showAndWait();
            }

        });

        deletebutton.setOnAction(e -> {
            String filename = " ";
            List<File> selectedItems = AddDeleteListview.getSelectionModel().getSelectedItems();
            System.out.println(selectedItems);
            if (!AddDeleteListview.getSelectionModel().getSelectedItems().isEmpty()) {
                for (File filePath : selectedItems) {
                    try {
                        Files.move(Paths.get(filePath.getPath()), Paths.get("D:\\IntllijProjectFolder\\FirstBrowser\\Removedtxtfiles\\" + filePath.getName()));
                        filename = filename + " " + filePath.getName();
                    } catch (IOException ex) {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setTitle("ERROR");
                        a.setContentText("Failed to add the file!");
                        a.showAndWait();
                    }
                }
                AddDeleteListview.getItems().removeAll(selectedItems);
                try {
                    tester.createIndex();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("SUCCESS");
                a.setContentText("File(s) " + filename + " deleted successfully!");
                a.showAndWait();
            } else {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("ERROR");
                a.setContentText("No selected file");
                a.showAndWait();
            }
        });

        dialogAddDeleteEdit.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        dialogAddDeleteEdit.setTitle("Add and Delete files");
        dialogAddDeleteEdit.setResizable(true);
        dialogAddDeleteEdit.getDialogPane().setPrefSize(480, 320);
        dialogAddDeleteEdit.showAndWait();


    }

    public void setResultstoList() {
        if (tester.getResults() != null) {
            String[] splitresults = tester.getResults().split(",");
            for (int i = 0; i < splitresults.length; i++) {
                resultslist.getItems().addAll(splitresults[i]);
            }
            hitsString = tester.getHits();
        }
    }

    @FXML
    void editFile(ActionEvent event) {
        String currentLine = "";
        String allcont = "";
        String body = "";
        String people = "";
        String title = "";
        String places = "";
       // String allFilesContent = "";
        FileChooser fileEditChooser = new FileChooser();
        fileEditChooser.setTitle("Open Resource File");
        fileEditChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFiletoEdit = fileEditChooser.showOpenDialog(null);

        if (selectedFiletoEdit == null) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("ERROR");
            a.setContentText("File is not valid!");
            a.showAndWait();
        }else {
            try {
                BufferedReader brEditFile = new BufferedReader(new FileReader(selectedFiletoEdit));
                while ((currentLine = brEditFile.readLine()) != null) {
                    //System.out.println (currentLine);
                    allcont = allcont + " " + currentLine;
                }
                if (allcont.contains("<BODY>")) {
                    int pFrom = allcont.indexOf("<BODY>") + "<BODY>".length();
                    int pTo = allcont.lastIndexOf("</BODY>");
                    body = allcont.substring(pFrom, pTo);
                    //System.out.println (body);
                }
                if (allcont.contains("<PEOPLE>")) {
                    int pFrom = allcont.indexOf("<PEOPLE>") + "<PEOPLE>".length();
                    int pTo = allcont.lastIndexOf("</PEOPLE>");
                    people = allcont.substring(pFrom, pTo);
                    // System.out.println (people);
                }
                if (allcont.contains("<TITLE>")) {
                    int pFrom = allcont.indexOf("<TITLE>") + "<TITLE>".length();
                    int pTo = allcont.lastIndexOf("</TITLE>");
                    title = allcont.substring(pFrom, pTo);
                    // System.out.println (title);
                }
                if (allcont.contains("<PLACES>")) {
                    int pFrom = allcont.indexOf("<PLACES>") + "<PLACES>".length();
                    int pTo = allcont.lastIndexOf("</PLACES>");
                    places = allcont.substring(pFrom, pTo);
                    // System.out.println (places);
                }
                Stage stage = (Stage) bp.getScene().getWindow();
                Dialog<BorderPane> dialogEdit = new Dialog<>();
                dialogEdit.initOwner(stage);
                BorderPane Editbp = new BorderPane();
                Button UpdateEditFileButton = new Button("Update");
                Label EditFileName = new Label(selectedFiletoEdit.getName());
                Label labelPlaces =new Label("Places:");
                Label labelPeople =new Label("People:");
                Label labelTitle =new Label("Title:");
                Label labelBody =new Label("Body:");
                TextArea editFilePlaces = new TextArea(places);
                TextArea editFilePeople = new TextArea(people);
                TextArea editFileTitle = new TextArea(title);
                TextArea editFileBody = new TextArea(body);

                VBox EditVbox = new VBox();
                EditVbox.getChildren().addAll(EditFileName,labelPlaces,editFilePlaces,labelPeople,editFilePeople,labelTitle,editFileTitle,labelBody,editFileBody,UpdateEditFileButton);
                EditVbox.setAlignment(Pos.CENTER);
                Editbp.setCenter(EditVbox);

                UpdateEditFileButton.setOnAction(e->{

                    try {
                        PrintWriter writer = new PrintWriter(selectedFiletoEdit.getPath());
                        writer.print("");
                        writer.close();
                        RandomAccessFile newfileafterEdit = new RandomAccessFile(new File(selectedFiletoEdit.getPath()), "rw");
                        newfileafterEdit.writeBytes("<PLACES>" + editFilePlaces.getText() + "</PLACES>");
                        System.out.println(editFilePlaces.getText());
                        newfileafterEdit.writeBytes("\n");
                        newfileafterEdit.writeBytes("<PEOPLE>" + editFilePeople.getText() + "</PEOPLE>");
                        System.out.println(editFilePeople.getText());
                        newfileafterEdit.writeBytes("\n");
                        newfileafterEdit.writeBytes("<TITLE>" + editFileTitle.getText() + "</TITLE>");
                        System.out.println(editFileTitle.getText());
                        newfileafterEdit.writeBytes("\n");
                        newfileafterEdit.writeBytes("<BODY>" + editFileBody.getText() + "</BODY>");
                        System.out.println(editFileBody.getText());
                        newfileafterEdit.close();
                        try {
                            tester.createIndex();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        Alert a = new Alert(AlertType.INFORMATION);
                        a.setTitle("SUCCESS");
                        a.setContentText("File(s) " + selectedFiletoEdit.getName() + " updated successfully!");
                        a.showAndWait();
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    dialogEdit.close();
                });

                dialogEdit.getDialogPane().setContent(Editbp);
                dialogEdit.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                dialogEdit.setTitle("Edit file");
                dialogEdit.setResizable(true);
                dialogEdit.getDialogPane().setPrefSize(480, 320);
                dialogEdit.showAndWait();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }catch (IOException ioException){
                ioException.printStackTrace();
            }

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tester.start();
    }
}
