import khttp.get
import java.net.URLEncoder
import kotlin.system.exitProcess

val API_URL = "http://api.openweathermap.org/data/2.5/weather"

fun main(args: Array<String>) {
    if (args.isEmpty()) { // No args
        println("Please add a key for the Open Weather API in the launch arguments!")
        exitProcess(-1)
    } else if (args.size > 1) { // Too many args
        println("You've entered too many launch arguments!")
        exitProcess(-1)
    }

    print(".: WEATHER API :.")
    do {
        print("\nEnter city name:\n> ")
        var city = readLine()
        if (!city.isNullOrEmpty()) {
            val query = "$API_URL?APPID=${args[0]}&units=metric&q=" + URLEncoder.encode(city, "UTF-8")
            val json = get(query).jsonObject
            val responseCode = json.getInt("cod")

            // Error Check
            if (responseCode.toString().startsWith('2').not()) {
                when(responseCode) {
                    400, 401 -> {
                        println("Invalid API key provided!")
                        exitProcess(1)
                    }
                    404, 502 -> {
                        println("That's not a valid city!")
                    }
                    else -> {
                        println("Request failed with the following response $json")
                    }
                }
            } else {
                city = "${json.getString("name")}, ${json.getJSONObject("sys").getString("country")}"
                val temp = json.getJSONObject("main").getInt("temp")
                val weather = json.getJSONArray("weather").getJSONObject(0).getString("main")

                println("\nInformation for $city.." +
                        "\nTemperature: $temp°C" +
                        "\nWeather: $weather\n")
            }

            print(".. ")
            Thread.sleep(1000)
            print("Ask again? ..\n> ")
        }
    } while (readLine()!!.toLowerCase() in listOf("yes", "yea", "yee", "ye", "yh", "y", "1", "true", "sure", "ok"))
    println("Goodbye!")
}