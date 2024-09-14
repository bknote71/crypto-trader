import SwiftUI

@main
struct crypto_trader_macosApp: App {
  var body: some Scene {
    WindowGroup {
      MainView()
        .fixedSize()
        .environmentObject(CryptoViewModel.shared)
        .environmentObject(CandleViewModel())
        .environmentObject(OrderViewModel())
        .environmentObject(UserViewModel())
    }
    .windowResizability(.contentSize)
  }
}
