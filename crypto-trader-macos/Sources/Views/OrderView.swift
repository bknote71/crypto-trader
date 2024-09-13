import SwiftUI

struct OrderView: View {
  @EnvironmentObject private var orderViewModel: OrderViewModel
  
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        tabButton(side: .bid, selectedSide: $orderViewModel.selectedSide)
        tabButton(side: .ask, selectedSide: $orderViewModel.selectedSide)
        tabButton(side: .other, selectedSide: $orderViewModel.selectedSide)
        tabButton(side: .other, selectedSide: $orderViewModel.selectedSide)
      }
      
      OrderSideView(side: $orderViewModel.selectedSide)
    }
    .frame(width: 460, height: 360)
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

struct PriceStepperView: View {
  @Binding var price: Double
  var title: String
    
  var body: some View {
    HStack(spacing: 0) {
      Text(title)
      Spacer()
      HStack(spacing: 0) {
        Spacer()
        Text(price.formattedPrice())
          .padding(.trailing, 12)
        
        Button{
          price -= 1
        } label: {
          Image(systemName: "minus")
            .frame(width: 40, height: 40)
            .background(Color.gray100)
        }
        .buttonStyle(BorderlessButtonStyle())
        
        Color(Color.gray200)
          .frame(width: 1)
        
        Button {
          price += 1
        } label: {
          Image(systemName: "plus")
            .frame(width: 40, height: 40)
            .background(Color.gray100)
        }
        .buttonStyle(BorderlessButtonStyle())
        .padding(0)
      }
      .padding(0)
      .frame(width: 320, height: 40)
      .overlay(
        Rectangle()
          .stroke(Color.gray150, lineWidth: 1)
      )
    }
  }
}

#Preview {
  OrderView()
}
