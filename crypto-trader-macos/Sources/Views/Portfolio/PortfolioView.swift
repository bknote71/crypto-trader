import Charts
import SwiftUI

enum PortfolioItem {
  case assetlist
  case orderlist
  
  var title: String {
    switch self {
    case .assetlist:
      return "보유자산"
    case .orderlist:
      return "거래내역"
    }
  }
}

struct PortfolioView: View {
  @EnvironmentObject var userViewModel: UserViewModel
  
  @State private var selectedItem = PortfolioItem.assetlist
  
  var body: some View {
    VStack(spacing: 0) {
      HStack {
        tabBar
        Spacer()
      }
      Color(Color.gray100)
        .frame(height: 1)
      
      switch selectedItem {
      case .assetlist:
        AssetListView()
      case .orderlist:
        OrderListView()
      }
      
      Spacer()
    }
    .frame(width: TradingPriceViewConst.width)
    .background(.white)
    .onAppear {
      userViewModel.fetchUserInfo()
    }
  }
  
  var tabBar: some View {
    HStack(spacing: 0) {
      TabButton(title: PortfolioItem.assetlist.title, isSelected: selectedItem == .assetlist, width: 150, spacing: 9) {
        selectedItem = .assetlist
      }
      
      TabButton(title: PortfolioItem.orderlist.title, isSelected: selectedItem == .orderlist, width: 150, spacing: 9) {
        selectedItem = .orderlist
      }
      // 미체결, ..
    }
  }
  
  var columnDivider: some View {
    Color(Color.gray100)
      .frame(width: 1)
  }
}


#Preview {
    PortfolioView()
    .environmentObject(UserViewModel.shared)
}
