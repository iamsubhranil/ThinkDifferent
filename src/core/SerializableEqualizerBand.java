/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;
import javafx.scene.media.EqualizerBand;

/**
 *
 * @author Subhra
 */
public class SerializableEqualizerBand implements Serializable{
    private static final long serialVersionUID = 2466465248372300469L;
    
    private final double centerFrequency;
    private final double bandwidth;
    private final double gain;

    public SerializableEqualizerBand(EqualizerBand b) {
        this.centerFrequency = b.getCenterFrequency();
        this.bandwidth = b.getBandwidth();
        this.gain = b.getGain();
    }

    public double getCenterFrequency() {
        return centerFrequency;
    }

    public double getBandwith() {
        return bandwidth;
    }

    public double getGain() {
        return gain;
    }
    
    public EqualizerBand getBand(){
        return new EqualizerBand(centerFrequency,bandwidth,gain);
    }
    
}
