import SwiftUI

struct MainView: View {
  var body: some View {
    HStack(alignment: .top, spacing: 8) {
      TradingPriceView()
      CryptoListView()
    }
    .background(.gray)
    .frame(width: 950 + 400 + 8, height: 880)
  }
}

#Preview {
    MainView()
}
