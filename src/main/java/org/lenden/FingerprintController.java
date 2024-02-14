package org.lenden;
/*

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class FingerprintController implements Initializable {


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        FingerprintImage image = null;
        try {
            var probe = new FingerprintTemplate(
                    new FingerprintImage(Files.readAllBytes(Paths.get("src/main/resources/candidate - Copy.tif"))));
            var candidate = new FingerprintTemplate(
                    new FingerprintImage(Files.readAllBytes(Paths.get("src/main/resources/candidate.tif"))));
            var matcher = new FingerprintMatcher(probe);
            double similarity = matcher.match(candidate);

            if(similarity>=150)
            {
                System.out.println("MATCH");
            }
            else
            {
                System.out.println("NON-MATCH");
            }
            System.out.println(similarity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

*/