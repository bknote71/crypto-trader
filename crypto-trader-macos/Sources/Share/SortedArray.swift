struct SortedArray<Element: Comparable> {
  private var elements: [Element] = []

  mutating func insert(_ newElement: Element) {
    let index = elements.binarySearch { $0 < newElement }
    elements.insert(newElement, at: index)
  }

  mutating func update(where predicate: (Element) -> Bool, with newElement: Element) {
    if let index = elements.firstIndex(where: predicate) {
      elements.remove(at: index)
      insert(newElement)
    }
  }
  
  mutating func insertAll(_ values: [Element]) {
    for value in values {
      insert(value)
    }
  }
  
  func allElements() -> [Element] {
    return elements
  }
  
  var indices: Range<Int> {
    return elements.indices
  }

  subscript(index: Int) -> Element {
    return elements[index]
  }
  
  func firstIndex(where predicate: (Element) -> Bool) -> Int? {
    return elements.firstIndex(where: predicate)
  }
}


extension Array where Element: Comparable {
  // FFTT 구조: 크거나 같은 놈들
  func binarySearch(_ isOrderedBefore: (Element) -> Bool) -> Index {
    var low = startIndex
    var high = endIndex
    
    while low < high {
      let mid = index(low, offsetBy: distance(from: low, to: high) / 2)
      if isOrderedBefore(self[mid]) {
        low = index(after: mid)
      } else {
        high = mid
      }
    }
    return low
  }
}
