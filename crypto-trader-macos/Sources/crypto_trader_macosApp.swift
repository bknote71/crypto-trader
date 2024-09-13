import SwiftUI

@main
struct crypto_trader_macosApp: App {
  var body: some Scene {
    WindowGroup {
      MainView()
        .fixedSize()
        .environmentObject(TickerViewModel())
        .environmentObject(CandleViewModel())
        .environmentObject(OrderViewModel())
    }
    .windowResizability(.contentSize)
  }
}
