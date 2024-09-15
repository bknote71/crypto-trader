import SwiftUI

struct TradingPriceView: View {
  @EnvironmentObject private var candleViewModel: CandleViewModel
  @EnvironmentObject private var tickerViewModel: CryptoViewModel
  
  @State private var selectedTab: String = "시세"
  
  var body: some View {
    VStack(spacing: 0) {
      if tickerViewModel.crypto != nil {
        header
        divider
        if selectedTab == "시세" {
          chartView
        } else {
          Text("정보 뷰")
        }
      } else {
        Text("데이터가 없습니다.")
      }
    }
    .frame(width: 950, height: 500)
  }
  
  var header: some View {
    return HStack(alignment: .center, spacing: 0) {
      HStack(spacing: 0) {
        Image(systemName: "bitcoinsign.circle.fill")
          .resizable()
          .frame(width: 24, height: 24)
          .padding(.trailing, 4)
          .foregroundStyle(.orange)
        
        Text(tickerViewModel.crypto!.nameKr)
          .font(.title3)
          .padding(.trailing, 2)
        
        Text(tickerViewModel.crypto!.nameEn)
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
      tabButton(title: "시세", isSelected: selectedTab == "시세") {
        selectedTab = "시세"
      }
      tabButton(title: "정보", isSelected: selectedTab == "정보") {
        selectedTab = "정보"
      }
    }
  }
  
  func tabButton(title: String, isSelected: Bool, action: @escaping () -> Void) -> some View {
    VStack(alignment: .center, spacing: 9) {
      Text(title)
        .font(.title3)
        .foregroundColor(isSelected ? .blue : .black)
        .padding(.top, 12)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? .blue : .clear)
    }
    .frame(width: 120)
    .contentShape(Rectangle())
    .onTapGesture {
      action()
    }
  }
  
  @ViewBuilder
  var chartView: some View {
    chartHeaderView
    divider
    chartMainView
  }
  
  var chartHeaderView: some View {
    HStack {
      // 왼쪽 시세 정보
      VStack(alignment: .leading, spacing: 4) {
        Text("\(String(tickerViewModel.crypto!.ticker.tradePrice)) KRW")
          .font(.title)
          .foregroundColor(.blue)
        HStack {
          Text("-0.54%")
            .foregroundColor(.blue)
          Text("▼ -400,000")
            .foregroundColor(.blue)
        }
        .font(.subheadline)
      }
      Spacer()
      
      // 오른쪽 상세 정보
      HStack(alignment: .center, spacing: 12) {
        VStack(spacing: 8) {
          HStack {
            Text("고가")
            Spacer()
            Text("73,814,000")
              .foregroundColor(.red)
          }
          divider
          HStack {
            Text("저가")
            Spacer()
            Text("73,205,000")
              .foregroundColor(.blue)
          }
        }
        .frame(width: 160)
        
        VStack(spacing: 8) {
          HStack {
            Text("거래량(24h)")
            Spacer()
            Text(String(tickerViewModel.crypto!.ticker.accTradePrice24h))
          }
          divider
          HStack {
            Text("거래대금(24H)")
            Spacer()
            Text("434,285,971,832 KRW")
          }
        }
        .frame(width: 200)
      }
      .font(.subheadline)
    }
    .padding(8)
    .background(.white)
  }
  
  var chartMainView: some View {
    CandleChartView()
  }
}

#Preview {
  TradingPriceView()
}

