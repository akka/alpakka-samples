# Step 4: Producing JSON

### Description

The helper method `toJson` turns the maps into JSON by using @scala[[Spray JSON](https://github.com/spray/spray-json)]@java[Jackson].

@scala[The JSON structure is converted to `String`s via `compactPrint`.]
