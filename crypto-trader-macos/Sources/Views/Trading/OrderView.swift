import SwiftUI

struct OrderView: View {
  @EnvironmentObject private var orderViewModel: OrderViewModel
  @EnvironmentObject private var userViewModel: UserViewModel
  
  @State var orderAction: Bool = false
  
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        tabButton(side: .BID, selectedSide: $orderViewModel.currentSide)
        tabButton(side: .ASK, selectedSide: $orderViewModel.currentSide)
        tabButton(side: .other, selectedSide: $orderViewModel.currentSide)
        tabButton(side: .other, selectedSide: $orderViewModel.currentSide)
      }
      
      OrderDetailView(side: $orderViewModel.currentSide, orderAction: $orderAction)
    }
    .frame(width: OrderViewConst.width, height: OrderViewConst.height)
    .padding(0)
    .background(.white)
    .if(orderAction) {
      $0
        .overlay {
          ZStack {
            Color.black.opacity(0.7)
              .edgesIgnoringSafeArea(.all)
            
            VStack(alignment: .center, spacing: 0) {
              Text("주문하시겠습니까?")
                .font(.title)
                .padding(.top, 24)
                .padding(.bottom, 24)
              
              HStack(spacing: 8) {
                Button {
                  orderAction = false
                  orderViewModel.order(account: userViewModel.user!.account)
                } label: {
                  Text("주문")
                    .padding(.vertical, 16)
                    .padding(.horizontal, 24)
                    .background(
                      orderViewModel.currentSide == .BID ? .red : .blue
                    )
                    .foregroundColor(.white)
                }
                .buttonStyle(PlainButtonStyle())
                
                Button {
                  orderAction = false
                } label: {
                  Text("취소")
                    .padding(.vertical, 16)
                    .padding(.horizontal, 24)
                    .background(Color.gray200)
                    .foregroundColor(.white)
                }
                .buttonStyle(PlainButtonStyle())
              }
            }
            .padding(.horizontal, 24)
            .padding(.vertical, 20)
            .background(Color.white)
          }
        }
    }
  }
  
  func tabButton(side: OrderSide, selectedSide: Binding<OrderSide>) -> some View {
    let isSelected = selectedSide.wrappedValue == side
    let title = side.title
    let color = side.color
    
    return VStack(spacing: 9) {
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
