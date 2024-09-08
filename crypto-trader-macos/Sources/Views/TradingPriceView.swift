import SwiftUI
import DGCharts

struct TradingPriceView: View {
  @State private var selectedTab: String = "시세"
  
  var body: some View {
    VStack(spacing: 0) {
      header
      divider
      if selectedTab == "시세" {
        chartView
      } else {
        Text("정보 뷰")
      }
    }
    .frame(width: 950, height: 500)
  }
  
  var header : some View {
    HStack(spacing: 0) {
      HStack(spacing: 0) {
        Image(systemName: "bitcoinsign.circle.fill")
          .resizable()
          .frame(width: 24, height: 24)
          .padding(.trailing, 4)
          .foregroundStyle(.orange)
        
        Text("비트코인")
          .font(.title3)
          .padding(.trailing, 2)
        
        Text("BTC/KRW")
          .font(.system(size: 8))
      }
      .padding(.bottom, 8)

      Spacer()
      tabBar
    }
    .padding(.horizontal, 8)
    .padding(.top, 8)
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
    VStack(alignment: .center, spacing: 8) {
      Text(title)
        .font(.title3)
        .foregroundColor(isSelected ? .blue : .black)
        .padding(.top, 8)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? .blue : .clear)
    }
    .frame(width: 100)
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
        Text("73,244,000 KRW")
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
            Text("5,830.751 BTC")
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
    CandleStickChartViewWrapper(entries: dummyData())
  }
  
  func dummyData() -> [CandleChartDataEntry] {
    var dummy = [CandleChartDataEntry]()
    var previousClose: Double = 100.0
    let emptyIndices: Set<Int> = Set(75...80)
    
    for i in 1...80 {
      if emptyIndices.contains(i) {
        let emptyEntry = CandleChartDataEntry(x: Double(i), shadowH: 0, shadowL: 0, open: 0, close: 0)
        dummy.append(emptyEntry)
        continue
      }
      
      let high = previousClose + Double.random(in: 0...30)
      let low = previousClose - Double.random(in: 0...30)
      let close = Double.random(in: low...high)
      let open = previousClose // open은 이전 close 값
      
      // 새로운 CandleChartDataEntry 생성
      let entry = CandleChartDataEntry(x: Double(i), shadowH: high, shadowL: low, open: open, close: close)
      
      // 엔트리를 배열에 추가
      dummy.append(entry)
      
      // 현재 close 값을 다음 루프에서 사용할 수 있도록 저장
      previousClose = close
    }
    
    return dummy
  }
}

#Preview {
  TradingPriceView()
}

