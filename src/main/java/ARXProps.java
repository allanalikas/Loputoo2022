import org.ini4j.Wini;
import java.io.File;
import java.io.IOException;


public class ARXProps {

    private float suppressionLimit;

    private int KAnonymity;

    private int LocalIterationNumber;

    private int LDiversity;

    public ARXProps(){};

    public static ARXProps createARXProperties(String fileName) throws IOException {
        ARXProps props = new ARXProps();
        Wini ini = new Wini(new File(fileName));

        props.setSuppressionLimit(ini.get("ARX", "suppressionlimit", float.class));
        props.setKAnonymity(ini.get("ARX", "kanonymity", int.class));
        props.setLDiversity(ini.get("ARX", "ldiversity", int.class));
        props.setLocalIterationNumber(ini.get("ARX", "localiterationnumber", int.class));

        return props;
    };

    public float getSuppressionLimit() {
        return suppressionLimit;
    }

    public void setSuppressionLimit(float suppressionLimit) {
        this.suppressionLimit = suppressionLimit;
    }

    public int getKAnonymity() {
        return KAnonymity;
    }

    public void setKAnonymity(int KAnonymity) {
        this.KAnonymity = KAnonymity;
    }

    public int getLocalIterationNumber() {
        return LocalIterationNumber;
    }

    public void setLocalIterationNumber(int localIterationNumber) {
        LocalIterationNumber = localIterationNumber;
    }

    public int getLDiversity() {
        return LDiversity;
    }

    public void setLDiversity(int LDiversity) {
        this.LDiversity = LDiversity;
    }
}
