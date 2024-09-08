import SwiftUI

struct MainView: View {
  var body: some View {
    HStack(alignment: .top, spacing: 8) {
      VStack(spacing: 8) {
        TradingPriceView()
        HStack(spacing: 8) {
          AskPriceView()
          OrderView()
        }
      }
      CryptoListView()
    }
    .background(Color.gray100)
    .frame(width: 950 + 400 + 8, height: 868)
  }
}

#Preview {
    MainView()
}
