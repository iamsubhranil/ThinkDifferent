/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.Serializable;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;

/**
 *
 * @author Subhra
 */
public class SerializableAudioEqualizer implements Serializable{
    private static final long serialVersionUID = -4451485001264151681L;
    
    private final SerializableEqualizerBand [] seq;
    
    private int i = 0;
    
    public SerializableAudioEqualizer(AudioEqualizer ae){
        seq = new SerializableEqualizerBand[ae.getBands().size()];
        ae.getBands().forEach((a)->{
            seq[i] = new SerializableEqualizerBand(a);
            i++;
        });
    }

    public EqualizerBand[] getEqualizerBands() {
        EqualizerBand [] eb = new EqualizerBand[seq.length];
            int i2 = 0;
        for(SerializableEqualizerBand s : seq){
            eb[i2] = s.getBand();
            i2++;
        }
        return eb;
    }
    
    
}
