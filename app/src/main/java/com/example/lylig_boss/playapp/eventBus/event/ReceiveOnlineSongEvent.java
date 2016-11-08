package com.example.lylig_boss.playapp.eventBus.event;

import com.example.lylig_boss.playapp.models.OnlineSong;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 28/06/2016.
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class ReceiveOnlineSongEvent {
    private OnlineSong onlineSong;
    private int positionSong;
}
