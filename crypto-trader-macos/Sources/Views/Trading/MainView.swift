import SwiftUI

struct MainView: View {
  enum SidebarItem {
    case exchange
    case portfolio
  }
  
  @State private var selectedSidebarItem: SidebarItem = .exchange
  
  var body: some View {
    HStack(alignment: .top, spacing: 8) {
      sidebarView
      switch selectedSidebarItem {
      case .exchange:
        exchangeView
      case .portfolio:
        portfolioView
      }
      CryptoListView()
    }
    .background(Color.gray.opacity(0.1))
    .frame(height: MainViewConst.height)
  }
  
  var sidebarView: some View {
    VStack(alignment: .center, spacing: 0) {
      Button(action: {
        selectedSidebarItem = .exchange
      }) {
        VStack {
          Image(systemName: "building.2.crop.circle") // 거래소 관련 이미지
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: 30, height: 30)
          
          Text("거래소")
            .font(.caption)
        }
        .frame(width: 60, height: 60)
        .background(.white) // 배경이 적용되야 터치가 적용된다.
      }
      .buttonStyle(PlainButtonStyle())
      .contentShape(Rectangle())
      
      divider
      
      Button(action: {
        selectedSidebarItem = .portfolio
      }) {
        VStack {
          Image(systemName: "person.circle") // 인간 관련 이미지
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: 30, height: 30)
          
          Text("포트폴리오")
            .font(.caption)
        }
        .frame(width: 60, height: 60)
        .background(.white)
      }
      .buttonStyle(PlainButtonStyle())
      .contentShape(Rectangle())
      
      divider
      
      Spacer()
    }
    .frame(width: 60)
    .background(.white)
  }
  
  var exchangeView: some View {
    VStack(spacing: 8) {
      TradingPriceView()
      HStack(spacing: 8) {
        AskPriceView()
        OrderView()
      }
    }
  }
  
  var portfolioView: some View {
    PortfolioView()
  }
  
  var divider: some View{
    Color(Color.gray100)
      .frame(height: 1)
  }
}

#Preview {
    MainView()
}
