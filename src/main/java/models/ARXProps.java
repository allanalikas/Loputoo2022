package models;

import org.ini4j.Wini;
import java.io.File;
import java.io.IOException;


public class ARXProps {

    /**
     * Value describing how much percentage of data is allowed to be generalized fully.
     */
    private float suppressionLimit;

    /**
     * K-Anonymity value.
     */
    private int KAnonymity;

    /**
     * Anonymization iterations.
     */
    private int LocalIterationNumber;

    /**
     * L-diversity value.
     */
    private int LDiversity;

    public ARXProps(String fileName) throws IOException {
        Wini ini = new Wini(new File(fileName));
        this.suppressionLimit = ini.get("ARX", "suppressionlimit", float.class);
        this.KAnonymity = ini.get("ARX", "kanonymity", int.class);
        this.LocalIterationNumber = ini.get("ARX", "localiterationnumber", int.class);
        this.LDiversity = ini.get("ARX", "ldiversity", int.class);
    };

    public float getSuppressionLimit() {
        return suppressionLimit;
    }

    public int getKAnonymity() {
        return KAnonymity;
    }

    public int getLocalIterationNumber() {
        return LocalIterationNumber;
    }

    public int getLDiversity() {
        return LDiversity;
    }

}
