package com.example.lylig_boss.playapp.eventBus.event;

import com.example.lylig_boss.playapp.models.OfflineSong;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 28/06/2016.
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class ReceiveOfflineSongEvent {
    private OfflineSong offlineSong;
    private int positionSong;
}
