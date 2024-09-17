import SwiftUI

enum TradingPriceItem {
  case chart
  case info
  
  var title: String {
    switch self {
    case .chart:
      "시세"
    case .info:
      "정보"
    }
  }
}

struct TradingPriceView: View {
  @EnvironmentObject private var cryptoViewModel: CryptoViewModel
  
  @State private var selectedItem: TradingPriceItem = .chart
  
  var body: some View {
    VStack(spacing: 0) {
      if cryptoViewModel.crypto != nil {
        header
        divider
        switch selectedItem {
        case .chart:
          ChartView()
        case .info:
          Text("--")
        }
      } else {
        Text("데이터가 없습니다.")
      }
    }
    .frame(width: TradingPriceViewConst.width, height: TradingPriceViewConst.height)
  }
  
  var header: some View {
    return HStack(alignment: .center, spacing: 0) {
      HStack(spacing: 0) {
        Image(systemName: "bitcoinsign.circle.fill")
          .resizable()
          .frame(width: 24, height: 24)
          .padding(.trailing, 4)
          .foregroundStyle(.orange)
        
        Text(cryptoViewModel.crypto!.nameKr)
          .font(.title3)
          .padding(.trailing, 2)
        
        Text(cryptoViewModel.crypto!.nameEn)
          .font(.system(size: 8))
      }

      Spacer()
      tabBar
    }
    .padding(.horizontal, 8)
    .background(.white)
  }
  
  var divider: some View {
    Color(.gray)
      .frame(height: 0.5)
      .padding(0)
  }
  
  var tabBar: some View {
    HStack(spacing: 0) {
      // 120  9
      TabButton(
        title: TradingPriceItem.chart.title,
        isSelected: selectedItem == .chart,
        width: 120,
        spacing: 9
      ) {
        selectedItem = .chart
      }
      TabButton(
        title: TradingPriceItem.info.title,
        isSelected: selectedItem == .info,
        width: 120,
        spacing: 9
      ) {
        selectedItem = .info
      }
    }
  }
}

#Preview {
  TradingPriceView()
}

