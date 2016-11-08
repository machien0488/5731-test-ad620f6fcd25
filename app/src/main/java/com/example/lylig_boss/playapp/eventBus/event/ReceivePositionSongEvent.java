package com.example.lylig_boss.playapp.eventBus.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by LyLiG_Boss on 24/06/2016.
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class ReceivePositionSongEvent {
    private int positionSong;
}
