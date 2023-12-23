//
//  ContentView.swift
//  sampleIOSApp
//
//  Created by Chukwuemeka Nwagu on 2023-12-22.
//

import SwiftUI
import forms

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text(Greeting().greet())
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
