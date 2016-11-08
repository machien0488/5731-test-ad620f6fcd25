package com.example.lylig_boss.playapp.eventBus.event;

import com.example.lylig_boss.playapp.models.OnlineSong;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 27/07/2016.
 */

@AllArgsConstructor(suppressConstructorProperties = true)
@Data
public class ReceiverDataOnlineEvent {
    private List<OnlineSong> onlineSongs;
}
