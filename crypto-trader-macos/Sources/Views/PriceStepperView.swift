import SwiftUI

struct PriceStepperView: View {
  @Binding var price: Double
  @FocusState private var isEditing: Bool
  
  var title: String
    
  var body: some View {
    HStack(spacing: 0) {
      Text(title)
      Spacer()
      HStack(spacing: 0) {
        DoubleTextField(value: $price, isEditing: isEditing)
          .padding(.trailing, 12)
          .focused($isEditing)
        
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
          .stroke(
            isEditing ? .blue : Color.gray150,
            lineWidth: 1
          )
      )
    }
  }
}

#Preview {
  PriceStepperView(price: .constant(0), title: "t")
}
