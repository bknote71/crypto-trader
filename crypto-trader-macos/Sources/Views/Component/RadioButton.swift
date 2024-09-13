import SwiftUI

struct RadioButton: View {
  let text: String
  @Binding var selected: String
    
  var body: some View {
    Button(action: {
      selected = text
    }) {
      HStack {
        Image(systemName: selected == text ? "largecircle.fill.circle" : "circle")
        Text(text)
      }
    }
    .buttonStyle(PlainButtonStyle())
  }
}
