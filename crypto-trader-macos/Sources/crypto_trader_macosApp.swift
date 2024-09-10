//
//  crypto_trader_macosApp.swift
//  crypto-trader-macos
//
//  Created by bknote71 on 9/7/24.
//

import SwiftUI

@main
struct crypto_trader_macosApp: App {
  var body: some Scene {
    WindowGroup {
      MainView()
        .fixedSize()
        .environmentObject(TickerViewModel())
        .environmentObject(CandleViewModel())
    }
    .windowResizability(.contentSize)
  }
}
