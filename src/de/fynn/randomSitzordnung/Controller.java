package de.fynn.randomSitzordnung;

import de.fynn.randomSitzordnung.database.DBConnector;
import de.fynn.randomSitzordnung.file.ConfigLoader;
import de.fynn.randomSitzordnung.file.FileHandler;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Controller{

    private final FileHandler fileHandler = new FileHandler();
    private final DBConnector dbConnector = new DBConnector();
    private final ConfigLoader cfgLoader = new ConfigLoader();
    private SeatingArrangementManager manager;
    private final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TXT Dateien (*.txt)","*.txt");
    private boolean isUsed = false;
    private boolean isPrepared = false;

    @FXML
    private Button create;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private TextField user;

    @FXML
    private TextField password;

    @FXML
    private TextField select;

    @FXML
    private TextField from;

    @FXML
    private TextField where;

    @FXML
    private Label selectedFile;

    @FXML
    private Label studentsAmount;

    @FXML
    private Label connectionStatus;

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField rows;

    @FXML
    private Label loadList;

    @FXML
    private MenuItem export;

    @FXML
    private TextField roomNumber;

    @FXML
    private TextField courseNumber;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private ColorPicker colorPicker2;

    @FXML
    private ComboBox<String> choiceBox;

    @FXML
    private VBox profiles;

    @FXML
    private TextField profileName;

    @FXML
    private Button createProfile;

    @FXML
    private ToggleButton useNamelist;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label currentProfile;

    @FXML
    private Button dbConnect;

    @FXML
    private Button selectFile;

    @FXML
    private Button loadFile;

    @FXML
    private Menu file;

    @FXML
    private Menu settings;

    @FXML
    private Menu help;

    @FXML
    private MenuItem importFile;

    @FXML
    private MenuItem resetSettings;

    @FXML
    private MenuItem ds;

    @FXML
    private MenuItem imp;

    private Label selecetedLabel = null;
    private boolean isCBPrepared = false;
    private boolean isVBPrepared = false;

    public Controller() {
        manager = new SeatingArrangementManager(new ArrayList<>(),cfgLoader.getRows());
        Main.getStage().widthProperty().addListener((observable,oldValue,newValue)-> setSeatingArrangement(manager.getSeatingArrangement()));
        Main.getStage().heightProperty().addListener((observable,oldValue,newValue)-> setSeatingArrangement(manager.getSeatingArrangement()));
        Main.getStage().fullScreenProperty().addListener((observable,oldValue,newValue)-> {
            loadColor();
            prepareChoicebox();
            prepareVBox();
            if(!isPrepared)addIcons();
        });
    }

    @FXML
    public void selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setTitle("Schülerliste laden");
        File f = new File(cfgLoader.getPathSelect());
        if(f.exists()){
            fileChooser.setInitialDirectory(new File(cfgLoader.getPathSelect()));
        }
        File file = fileChooser.showOpenDialog(Main.getStage());
        if(file==null){
            if(manager==null){
                create.setDisable(true);
            }
            return;
        }
        cfgLoader.setPathSelect(file.getParent());
        List<Student> students = fileHandler.loadStudentFile(file);
        studentsAmount.setText("Schüler: "+students.size());
        selectedFile.setText(file.getName());
        manager = new SeatingArrangementManager(students, cfgLoader.getRows());
        cfgLoader.saveStudentsToCFG(students);
        create.setDisable(false);
        useNamelist.setDisable(false);
        loadList.setText("");
    }

    @FXML
    public void connect(){
        String ip = this.ip.getText();
        String port = this.port.getText();
        String user = this.user.getText();
        String password = this.password.getText();
        String select = this.select.getText();
        String from = this.from.getText();
        String where = this.where.getText();
        try {
            dbConnector.connect(ip,port,user,password,select,from,where);
            connectionStatus.setText("✓");
            connectionStatus.setTextFill(Color.GREEN);
            List<Student> students = dbConnector.getStudentsFromDB();
            manager = new SeatingArrangementManager(students, cfgLoader.getRows());
            cfgLoader.saveStudentsToCFG(students);
            create.setDisable(false);
            loadList.setText("");
        }catch (SQLException e){
            connectionStatus.setText("✘");
            connectionStatus.setTextFill(Color.RED);
            create.setDisable(true);
            e.printStackTrace();
        }
    }

    @FXML
    public void generate(){
        isUsed = true;
        List<Student> students = manager.random();
        setSeatingArrangement(students);
        export.setDisable(false);
    }

    @FXML
    public void exportFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)","*.pdf"));
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setTitle("Sitzplan exportieren");
        File f = new File(cfgLoader.getPathExport());
        if(f.exists()){
            fileChooser.setInitialDirectory(new File(cfgLoader.getPathExport()));
        }
        File file = fileChooser.showSaveDialog(Main.getStage());
        if(file==null)return;
        cfgLoader.setPathExport(file.getParent());
        fileHandler.exportFile(file,manager.getSeatingArrangement(),roomNumber.getText(),courseNumber.getText());
    }

    @FXML
    public void importFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setTitle("Sitzplan importieren");
        File f = new File(cfgLoader.getPathImport());
        if(f.exists()){
            fileChooser.setInitialDirectory(new File(cfgLoader.getPathImport()));
        }
        File file = fileChooser.showOpenDialog(Main.getStage());
        if(file==null)return;
        cfgLoader.setPathImport(file.getParent());
        isUsed = true;
        List<Student> students = fileHandler.importFile(file);
        manager = new SeatingArrangementManager(students, cfgLoader.getRows());
        setSeatingArrangement(manager.getSeatingArrangement());
    }

    @FXML
    public void setRows(){
        int i;
        try{
            i = Integer.parseInt(rows.getText());
        }catch (NumberFormatException e){
            rows.setText("");
            return;
        }
        cfgLoader.setRows(i);
        if(manager!=null)setSeatingArrangement(manager.getSeatingArrangement());
    }

    @FXML
    public void clearRows(){
        rows.setText("");
    }

    @FXML
    public void displayDSB(){
        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(Main.getStage());
        AnchorPane layout = new AnchorPane();
        TextArea area = new TextArea(fileHandler.getDS());
        area.setEditable(false);
        area.setPrefSize(800,420);
        layout.getChildren().add(area);
        Scene scene = new Scene(layout, 800, 420);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();
    }

    @FXML
    public void displayImp(){
        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(Main.getStage());
        AnchorPane layout = new AnchorPane();
        TextArea area = new TextArea(fileHandler.getImp());
        area.setEditable(false);
        layout.getChildren().add(area);
        Scene scene = new Scene(layout, 300, 200);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();
    }

    @FXML
    public void resetSettings(){
        cfgLoader.reset();
        manager = new SeatingArrangementManager(new ArrayList<>(),cfgLoader.getRows());
        create.setDisable(true);
        selectedFile.setText("Keine Datei ausgewählt");
        studentsAmount.setText("Keine Schüler geladen");
        ip.setText("");
        port.setText("");
        user.setText("");
        password.setText("");
        select.setText("");
        from.setText("");
        where.setText("");
        pane.getChildren().clear();
        loadList.setText("");
        connectionStatus.setText("");
        export.setDisable(true);
        profileName.setText("");
        useNamelist.setDisable(true);
        currentProfile.setText("Aktuelles Profil: -");
        loadColor();
        profiles.getChildren().clear();
        prepareChoicebox();
        isUsed = false;
    }

    @FXML
    public void loadLastList(){
        List<Student> students = cfgLoader.getStudentsFromCFG();
        if(students.isEmpty()){
            loadList.setText("✘");
            loadList.setTextFill(Color.RED);
            return;
        }
        manager = new SeatingArrangementManager(students,cfgLoader.getRows());
        loadList.setText("✓");
        loadList.setTextFill(Color.GREEN);
        create.setDisable(false);
    }

    @FXML
    public void setColor(){
        if(cfgLoader.colorGradient()){
            String color = "#"+colorPicker.getValue().toString().substring(2,8);
            String color2 = "#"+colorPicker2.getValue().toString().substring(2,8);
            gridPane.setStyle("-fx-background-color: linear-gradient(to left, "+color2+","+color+")");
            cfgLoader.setColor(color);
            cfgLoader.setColor2(color2);
        }else {
            String color = "#"+colorPicker.getValue().toString().substring(2,8);
            gridPane.setStyle("-fx-background-color: "+color+";");
            cfgLoader.setColor(color);
        }
    }

    @FXML
    public void changeColorGradient(){
        if(choiceBox.getValue().equals("Farbverlauf")){
            colorPicker2.setVisible(true);
            cfgLoader.setColorGradient(true);
        }else {
            colorPicker2.setVisible(false);
            cfgLoader.setColorGradient(false);
        }
        loadColor();
    }

    @FXML
    public void checkCreateProfile(){
        if(profileName.getText().equals("")){
            createProfile.setDisable(true);
        }else {
            createProfile.setDisable(false);
        }
    }

    @FXML
    public void createProfile(){
        List<Student> students;
        if(useNamelist.isSelected()){
            students = manager.getSeatingArrangement();
        }else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(filter);
            fileChooser.setSelectedExtensionFilter(filter);
            fileChooser.setTitle("Schülerliste laden");
            File f = new File(cfgLoader.getPathSelect());
            if(f.exists()){
                fileChooser.setInitialDirectory(new File(cfgLoader.getPathSelect()));
            }
            File file = fileChooser.showOpenDialog(Main.getStage());
            if(file==null){
                createProfile.setDisable(true);
                return;
            }
            cfgLoader.setPathSelect(file.getParent());
            students = fileHandler.loadStudentFile(file);
        }
        Profile profile = new Profile(profileName.getText(),students);
        if(!roomNumber.getText().equals(""))profile.setRoom(roomNumber.getText());
        if(!courseNumber.getText().equals(""))profile.setCourseNumber(courseNumber.getText());
        cfgLoader.saveProfile(profile);
        addProfileEntry(profile);
        profileName.setText("");
        createProfile.setDisable(true);
    }

    public void selectLabel(Label label){
        if(selecetedLabel==null){
            selecetedLabel = label;
            label.setStyle("-fx-border-color: red;");
        }else if(selecetedLabel == label){
            label.setStyle("-fx-border-color: black;");
            selecetedLabel = null;
        }else {
            List<Student> students = manager.getSeatingArrangement();
            int index1 = pane.getChildren().indexOf(label);
            int index2 = pane.getChildren().indexOf(selecetedLabel);
            Student name1 = students.get(index1);
            Student name2 = students.get(index2);
            selecetedLabel.setStyle("-fx-border-color: black;");
            selecetedLabel.setText(name1.getName());
            label.setText(name2.getName());
            selecetedLabel = null;
            List<Student> list = manager.getSeatingArrangement();
            int index = list.indexOf(name1);
            list.set(list.indexOf(name2),name1);
            list.set(index,name2);
        }
    }

    private void setSeatingArrangement(List<Student> students){
        if(!isUsed)return;
        int size = students.size();
        List<Double[]> bounds = getBounds(size);
        pane.getChildren().clear();
        for (int i = 0; i < size; i++) {
            Label label = new Label(students.get(i).getName());
            label.setLayoutX(bounds.get(i)[0]);
            label.setLayoutY(bounds.get(i)[1]);
            label.setPrefSize(bounds.get(i)[2],bounds.get(i)[3]);
            label.setStyle("-fx-border-color: black;");
            label.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,null,null)));
            label.setAlignment(Pos.CENTER);
            label.setOnMouseClicked(e-> selectLabel(label));
            pane.getChildren().add(label);
        }
    }

    private List<Double[]> getBounds(int amount){
        List<Double[]> bounds = new ArrayList<>();
        double maxwidth = pane.getWidth()-20;
        double maxHeight = pane.getHeight()-20;
        int rows = cfgLoader.getRows();
        int[] studentsPerRow = studentsPerRow(amount,rows);
        double x = 0;
        double y = 10;
        double height = maxHeight/(rows*2-1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < studentsPerRow[i]; j++) {
                double width = maxwidth/(studentsPerRow[i]*2+1);
                x = x==0?width:x;
                Double[] value = new Double[]{
                        x,y,width,height
                };
                bounds.add(value);
                x+=width*2;
            }
            x = 0;
            y+=height*2;
        }
        return bounds;
    }

    private int[] studentsPerRow(int studentAmount, int rows){
        int[] studentsPerRow = new int[rows];
        for (int i = 0; i < studentAmount; i++) {
            studentsPerRow[i%rows]++;
        }
        return studentsPerRow;
    }

    private void addProfileEntry(Profile profile){
        HBox box = editProfileEntry(profile);
        profiles.getChildren().add(box);
    }

    private HBox editProfileEntry(Profile profile){
        HBox box = new HBox();
        box.setPrefSize(230,20);
        Label label = new Label(profile.getName());
        label.setPrefSize(60,20);
        Button laden = new Button("Laden");
        laden.setPrefSize(50,20);
        laden.setOnAction(event -> {
            roomNumber.setText(profile.getRoom());
            courseNumber.setText(profile.getCourseNumber());
            manager = new SeatingArrangementManager(profile.getStudents(),cfgLoader.getRows());
            create.setDisable(false);
            currentProfile.setText("Aktuelles Profil: "+profile.getName());
        });
        Button buttonEdit = new Button("Bearbeiten");
        buttonEdit.setPrefSize(70,20);
        buttonEdit.setOnAction(event -> {
            Profile temp = new Profile(profile.getName(), profile.getStudents());
            temp.setRoom(profile.getRoom());
            temp.setCourseNumber(profile.getCourseNumber());
            box.getChildren().clear();
            Profile editedProfile = edititProfile(profile);
            box.getChildren().addAll(editProfileEntry(editedProfile).getChildren());
            cfgLoader.editProfile(temp,editedProfile);
        });
        Button buttonDelete = new Button("Löschen");
        buttonDelete.setPrefSize(50,20);
        buttonDelete.setOnAction(event -> {
            profiles.getChildren().remove(box);
            cfgLoader.deleteProfile(profile);
        });
        box.getChildren().add(label);
        box.getChildren().add(laden);
        box.getChildren().add(buttonEdit);
        box.getChildren().add(buttonDelete);
        return box;
    }

    private Profile edititProfile(Profile profile){
        AtomicReference<List<Student>> students = new AtomicReference<>(profile.getStudents());
        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(Main.getStage());
        VBox box = new VBox();
        HBox box1 = new HBox();
        box1.setPrefSize(200,20);
        HBox box2 = new HBox();
        box2.setPrefSize(200,20);
        Button back = new Button("Abbrechen");
        back.setPrefSize(100,20);
        Button save = new Button("Speichern");
        save.setPrefSize(100,20);
        box1.getChildren().add(back);
        box1.getChildren().add(save);
        TextField room = new TextField(profile.getRoom());
        room.setPromptText("Raumnummer");
        room.setPrefSize(100,20);
        TextField coursenumber = new TextField(profile.getCourseNumber());
        coursenumber.setPromptText("Kursnummer / Klasse");
        coursenumber.setPrefSize(100,20);
        TextField name = new TextField(profile.getName());
        name.setPrefSize(200,20);
        name.setPromptText("Profilname");
        box2.getChildren().add(room);
        box2.getChildren().add(coursenumber);
        Button file =new Button("Namensliste ändern");
        file.setPrefSize(200,20);
        box.getChildren().add(name);
        box.getChildren().add(file);
        box.getChildren().add(box2);
        box.getChildren().add(box1);
        name.setOnKeyTyped(event -> {
            if(name.getText().equals("")){
                save.setDisable(true);
            }else {
                save.setDisable(false);
            }
        });
        file.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(filter);
            fileChooser.setSelectedExtensionFilter(filter);
            fileChooser.setTitle("Schülerliste laden");
            File f = new File(cfgLoader.getPathSelect());
            if(f.exists()){
                fileChooser.setInitialDirectory(new File(cfgLoader.getPathSelect()));
            }
            File temp = fileChooser.showOpenDialog(Main.getStage());
            if(temp==null)return;
            cfgLoader.setPathSelect(temp.getParent());
            students.set(fileHandler.loadStudentFile(temp));
        });
        save.setOnAction(event -> {
            profile.setName(name.getText());
            profile.setRoom(room.getText());
            profile.setCourseNumber(coursenumber.getText());
            profile.setStudents(students.get());
            popup.close();
        });
        back.setOnAction(event -> {
            popup.close();
        });
        Scene scene = new Scene(box, 200, 90);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.showAndWait();
        return profile;
    }

    private void prepareChoicebox(){
        if(isCBPrepared)return;
        ObservableList<String> list = choiceBox.getItems();
        list.add("Farbverlauf");
        list.add("Hintergrundfarbe");
        choiceBox.setItems(list);
        choiceBox.setValue(cfgLoader.colorGradient()?"Farbverlauf":"Hintergrundfarbe");
        isCBPrepared = true;
    }

    private void loadColor(){
        if(cfgLoader.colorGradient()){
            colorPicker2.setVisible(true);
            String color = cfgLoader.getColor();
            String color2 = cfgLoader.getColor2();
            gridPane.setStyle("-fx-background-color: linear-gradient(to left, "+color2+","+color+")");
            colorPicker.setValue(Color.web(color));
            colorPicker2.setValue(Color.web(color2));
        }else {
            String color = cfgLoader.getColor();
            gridPane.setStyle("-fx-background-color: "+color+";");
            colorPicker.setValue(Color.web(color));
        }
    }

    private void prepareVBox(){
        if(isVBPrepared)return;
        List<Profile> profiles = cfgLoader.getProfiles();
        for (Profile p:
             profiles) {
            addProfileEntry(p);
        }
        isVBPrepared = true;
    }

    private void addIcons(){
        Image image = new Image(getClass().getResourceAsStream("images/hammer_an.gif"));
        ImageView view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        create.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/addp.png"));
        view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        createProfile.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/dbver.png"));
        view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        dbConnect.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/select.png"));
        view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        selectFile.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/rl.png"));
        view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        loadFile.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/file.png"));
        view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        file.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/settings.png"));
        view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        settings.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/frag.png"));
        view = new ImageView(image);
        view.setFitHeight(25);
        view.setFitWidth(25);
        help.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/import.png"));
        view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        importFile.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/export.png"));
        view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        export.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/reset.png"));
        view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        resetSettings.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/imp.png"));
        view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        imp.setGraphic(view);
        image = new Image(getClass().getResourceAsStream("images/ds.png"));
        view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        ds.setGraphic(view);
    }

}
