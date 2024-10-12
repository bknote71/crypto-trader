package com.crypto_trader.api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CandlesInfo {
    int start;
    int end;
    int count;
    List<byte[]> candles;
}