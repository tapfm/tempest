package WeatherApp.Controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import WeatherApp.model.Field;
import WeatherApp.model.Weather;
import WeatherApp.service.AgroAPI;
import WeatherApp.service.AgroStore;
import WeatherApp.service.SettingsStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class FieldCapController implements Initializable{

    @FXML private Text fName;
    @FXML private Text fTemp;
    @FXML private Text fTempUnit;
    @FXML private Text fRain;
    @FXML private Text fRainUnit;
    @FXML private Text fWindspeed;
    @FXML private Text fWindspeedUnit;
    @FXML private Text latitude;
    @FXML private Text longitude;
    @FXML private Rectangle fColour;
    @FXML private ImageView TemperatureIcon;
    @FXML private ImageView RainfallIcon;
    @FXML private ImageView WindSpeedIcon;
    
    private Field field;
    private SettingsStore settingsStore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fName.setText("test");
    }

    public void setcoords(double lat, double lon) {
        latitude.setText(Double.toString(lat));
        longitude.setText(Double.toString(lon));
    }

    public void setName(String txt) {
        fName.setText(txt);
    }

    public void setRain(String txt) {
        fRain.setText(txt);
    }

    public void setRainUnit(String txt) {
        fRainUnit.setText(txt);
    }

    public void setTemp(String txt) {
        fTemp.setText(txt);
    }

    public void setTempUnit(String txt) {
        fTempUnit.setText(txt);
    }

    public void setWindspeed(String txt) {
        fWindspeed.setText(txt);
    }

    public void setWindspeedUnit(String txt) {
        fWindspeedUnit.setText(txt);
    }

    public void setField(Field field) {
        this.field = field;
        Color c = field.getColour();
        fColour.setFill(c);
        latitude.setText(Double.toString(field.getLat()).substring(0, 8));
        longitude.setText(Double.toString(field.getLng()).substring(0, 8));
        fName.setText(field.getName());
        
        //set the icons/weather symbols:
        File tempIconFile = new File("resources/weather-symbols/TemperatureIcon.png");
        Image itempIcon = new Image(tempIconFile.toURI().toString());
        TemperatureIcon.setImage(itempIcon);
        
        File RainfallIconFile = new File("resources/weather-symbols/RainfallIcon.png");
        Image iRainfallIcon = new Image(RainfallIconFile.toURI().toString());
        RainfallIcon.setImage(iRainfallIcon);
        
        File WindSpeedIconFile = new File("resources/weather-symbols/WindSpeedIcon.png");
        Image iWindSpeedIcon = new Image(WindSpeedIconFile.toURI().toString());
        WindSpeedIcon.setImage(iWindSpeedIcon);
    }

    public void setSettingsStore(SettingsStore settingsStore) {
        this.settingsStore = settingsStore;
    }

    private double convertTemp(Weather weather) {
        if (settingsStore.getTempUnit().equals("C"))
            return weather.getTemperature() - 273.15;
        else if (settingsStore.getTempUnit().equals("F"))
            return (weather.getTemperature() * 9) / 5 - 459.67;
        else
            return weather.getTemperature();
    }

    private double convertWind(Weather weather) {
        if (settingsStore.getWindUnit().equals("mph"))
            return weather.getWindSpeed() * 2.23694;
        else if (settingsStore.getWindUnit().equals("kmph"))
            return weather.getWindSpeed() * 3.6;
        else
            return weather.getWindSpeed();
    }

    public void updateToCurrentWeather() {
        try {

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(1);

            AgroStore agroStore = new AgroStore(Paths.get("stores/agroStore.json"), new AgroAPI(AgroAPI.loadApiKey()));
            Weather weather = agroStore.getCurrentWeather(field);
            fTemp.setText(nf.format(convertTemp(weather)));
            fTempUnit.setText(settingsStore.getTempUnitIcon());
            if(Double.isNaN(weather.getRainfall()))
                fRain.setText("0");
            else
                fRain.setText(nf.format(weather.getRainfall()));
            fWindspeed.setText(nf.format(convertWind(weather)));
            fWindspeedUnit.setText(settingsStore.getWindUnit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}