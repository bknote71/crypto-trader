import SwiftUI

struct OrderView: View {
  @EnvironmentObject private var orderViewModel: OrderViewModel
  
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        tabButton(side: .BID, selectedSide: $orderViewModel.currentSide)
        tabButton(side: .ASK, selectedSide: $orderViewModel.currentSide)
        tabButton(side: .other, selectedSide: $orderViewModel.currentSide)
        tabButton(side: .other, selectedSide: $orderViewModel.currentSide)
      }
      
      OrderDetailView(side: $orderViewModel.currentSide)
    }
    .frame(width: 452, height: 360)
    .padding(0)
    .background(.white)
  }
  
  func tabButton(side: OrderSide, selectedSide: Binding<OrderSide>) -> some View {
    let isSelected = selectedSide.wrappedValue == side
    let title = side.title
    let color = side.color
    
    return VStack(spacing: 12) {
      Text(title)
        .foregroundColor(isSelected ? color : .black)
        .padding(.top, 12)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? color : .clear)
    }
    .contentShape(Rectangle())
    .onTapGesture {
      selectedSide.wrappedValue = side
    }
  }
}

//#Preview {
//  OrderView()
//}
