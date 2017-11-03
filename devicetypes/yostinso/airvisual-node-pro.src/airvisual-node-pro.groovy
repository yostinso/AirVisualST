/**
 *  AirVisual Node Pro
 *
 *  Copyright 2017 E.O. Stinson
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "AirVisual Node Pro", namespace: "yostinso", author: "E.O. Stinson") {
		capability "Air Quality Sensor"
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
        capability "Sensor"
        capability "Polling"
        capability "Refresh"
        
        // attribute "humidity", "number" // from capability Relative Humidity Measurement
        // attribute "temperature", "number" // from capability Temperature Measurement

		// attribute "airQuality", "number" // from capability Air Quality Sensore
        attribute "aqiLevel", "enum", ["Good", "Moderate", "USG", "Unhealthy", "V. Unhealthy", "Hazardous"]
		attribute "hourlyAQI", "number"
		attribute "pm25", "number"
		attribute "hourlypm25", "number"
        
		attribute "co2Aqi", "number"
		attribute "co2AqiLevel", "enum", ["Good", "Moderate", "USG", "Unhealthy", "V. Unhealthy", "Hazardous"]
		attribute "co2", "number"
		attribute "hourlyco2", "number"
        
		attribute "pm10Aqi", "number"
		attribute "pm10AqiLevel", "enum", ["Good", "Moderate", "USG", "Unhealthy", "V. Unhealthy", "Hazardous"]
		attribute "pm10", "number"
		attribute "hourlypm10", "number"
		attribute "hourlyco2", "number"
        
		attribute "outdooraqi", "number"
		attribute "outdoorpm25", "number"
        
        // Display-only attributes
        attribute "pm25DisplayValue", "string"
        attribute "pm10DisplayValue", "string"
        attribute "co2DisplayValue", "string"
	}
    
    def icons = [
    	unknown: "https://airvisual.com/images/forecast_aqi404.png",
    	good: "https://airvisual.com/images/forecast_aqi1.png",
    	moderate: "https://airvisual.com/images/forecast_aqi2.png",
    	usg: "https://airvisual.com/images/forecast_aqi3.png",
    	unhealthy: "https://airvisual.com/images/forecast_aqi4.png",
    	very_unhealthy: "https://airvisual.com/images/forecast_aqi5.png",
    	hazardous: "https://airvisual.com/images/forecast_aqi6.png"
    ]

	tiles(scale: 2) {
    	standardTile("MainTile", "device.aqiLevel", width: 2, height: 2, icon: icons.unknown) {
        	state "unknown", label: '${name}', icon: icons.unknown, defaultState: true
            state "good", label: '${name}', icon: icons.good
            state "moderate", label: '${name}', icon: icons.moderate
            state "usg", label: '${name}', icon: icons.usg
            state "unhealthy", label: '${name}', icon: icons.unhealthy
            state "very_unhealthy", label: '${name}', icon: icons.very_unhealthy
            state "hazardous", label: '${name}', icon: icons.hazardous
        }
        
        def aqiTile = { name, attr, prefix ->
        	valueTile(name, attr, width: 2, height: 2) {
                state "unknown", label: prefix + ': UNKNOWN', icon: icons.unknown, defaultState: true
                state "good", label: prefix + ': GOOD', icon: icons.good
                state "moderate", label: prefix + ': MODERATE', icon: icons.moderate
                state "usg", label: prefix + ': USG', icon: icons.usg
                state "unhealthy", label: prefix + ': UNHEALTHY', icon: icons.unhealthy
                state "very_unhealthy", label: prefix + ': V. UNHEALTHY', icon: icons.very_unhealthy
                state "hazardous", label: prefix + ': HAZARDOUS', icon: icons.hazardous
            }
        } 
        def concTile = { name, attr, prefix ->
        	valueTile(name, attr, width: 2, height: 2) {
            	state "level", label: prefix + ":\n" + '${currentValue}', defaultState: true
            }
        }
        aqiTile("AQITile", "device.aqiLevel", "PM2.5")
        aqiTile("CO2Tile", "device.co2AqiLevel", "CO2")
        aqiTile("PM10Tile", "device.pm10AqiLevel", "PM10")
        concTile("PM25ConcTitle", "device.pm25DisplayValue", "PM 2.5")
        concTile("CO2ConcTitle", "device.co2DisplayValue", "CO2")
        concTile("PM10ConcTitle", "device.pm10DisplayValue", "PM 10")
        
        concTile("HourlyAQITile", "device.hourlyPm25DisplayValue", "Hourly PM2.5")
        concTile("HourlyCO2Tile", "device.hourlyCo2DisplayValue", "Hourly CO2")
        concTile("HourlyPM10Tile", "device.hourlyPm10DisplayValue", "Hourly PM10")
        
        multiAttributeTile(name: "TempAndHumidityTile", type: "thermostat", width: 4, height: 2) {
        	tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
            	attributeState("temp", label: '${currentValue}', unit: "dF", defaultState: true)
            }
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
            	attributeState("humidity", label: '${currentValue}%', unit: "%", defaultState: true)
            }
        }
        // TODO: Unified quality?
            	
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: flat, width: 2, height: 2) {
        	state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
        }
		main("MainTile")
        details([
            "AQITile",  "CO2Tile", "PM10Tile",
            "PM25ConcTitle", "CO2ConcTitle", "PM10ConcTitle",
            "HourlyAQITile", "HourlyCO2Tile", "HourlyPM10Tile",
            "TempAndHumidityTile", "refresh"
        ])
	}
}

def aqiText(aqi) {
	switch (aqi) {
    	case { it <= 50 }: return "good"
		case 51..100: return "moderate"
        case 101..150: return "usg"
        case 151..200: return "unhealthy"
        case 200..300: return "very_unhealthy"
        case { it > 300 }: return "hazardous"
    }
}

def refresh() {
	poll()
}

def poll() {
	def d = parent.getData()
    def currentAQI = currentAQI(d)
    def hourlyAQI = hourlyAQI(d)
    def lastHour = d.historical.hourly[0]
    def outdoor = lastHour.outdoor_station
    
    def wat = currentAQI.co2
    
    def values = [
		currentAQI: currentAQI,
        aqiText: aqiText(currentAQI.pm25),
        pm25: d.current.p2,
		hourlyAQI: hourlyAQI,
        co2AQI: currentAQI.co2,
        co2AQIText: aqiText(currentAQI.co2),
        pm10AQI: currentAQI.pm10,
        pm10AQIText: aqiText(currentAQI.pm10),
        pm10: d.current.p1,
        co2: d.current.co,
        hourlypm25: Math.round(lastHour.p2_sum / lastHour.p2_count),
        hourlypm10: Math.round(lastHour.p1_sum / lastHour.p1_count),
        hourlyco2: Math.round(lastHour.co_sum / lastHour.co_count),
        outdooraqi: outdoor.aqius,
        outdoorpm25: outdoor.p2.conc,
        humidity: d.current.hm,
        temperature: cToF(d.current.tp)
    ]
    
    log.debug "Air Quality: ${values}"
    
    sendEvent(name: "airQuality", 						value: currentAQI.pm25, 	description: aqiDescription(currentAQI), isStateChange: true)
    sendEvent(name: "aqiLevel", 						value: values.aqiText, 		description: aqiDescription(currentAQI))
    sendEvent(name: "hourlyAqi", displayed: false, 		value: hourlyAQI.pm25, 		description: "Hourly ${aqiDescription(hourlyAQI)}")
    sendEvent(name: "pm25", displayed: false, 			value: values.pm25,			description: "PM2.5: ${d.current.p2} ug/m^3")
    
    sendEvent(name: "co2Aqi", 							value: currentAQI.co2, 		description: aqiDescription(currentAQI), isStateChange: true)
    sendEvent(name: "co2AqiLevel", 						value: values.co2AQIText, 	description: aqiDescription(currentAQI))
    sendEvent(name: "co2", displayed: false, 			value: values.co2, 			description: "CO2: ${d.current.co} ppm")
    sendEvent(name: "hourlyco2", displayed: false, 		value: values.hourlyco2, 	description: "Hourly CO2: ${values.hourlyco2} ppm")
    
    sendEvent(name: "pm10Aqi", 							value: currentAQI.pm10, 	description: aqiDescription(currentAQI))
    sendEvent(name: "pm10AqiLevel",						value: values.pm10AQIText, 	description: aqiDescription(currentAQI))
    sendEvent(name: "pm10", displayed: false, 			value: values.pm10,			description: "PM10: ${d.current.p1} ug/m^3")
    sendEvent(name: "hourlypm25", displayed: false, 	value: values.hourlypm25, 	description: "Hourly PM2.5: ${values.hourlypm25} ug/m^3")
    sendEvent(name: "hourlypm10", displayed: false, 	value: values.hourlypm10, 	description: "Hourly PM10: ${values.hourlypm10} ug/m^3")
    
    sendEvent(name: "outdooraqi", displayed: false, 	value: values.outdooraqi, 	description: "Outdoor AQI: ${values.outdooraqi} (${outdoor.mainus})")
    sendEvent(name: "outdoorpm25", displayed: false, 	value: values.outdoorpm25,	description: "Outdoor PM2.5: ${values.outdoorpm25} ug/m^3")
    
    sendEvent(name: "humidity", displayed: false, 		value: values.humidity,		description: "Humidity: ${values.humidity}%")
    sendEvent(name: "temperature", displayed: false, 	value: values.temperature,	description: "Temperature: ${values.temperature} dF")
    
    // Display only
    sendEvent(name: "pm25DisplayValue", displayed: false, value: "${currentAQI.pm25}\n(${values.pm25} ug/m3)")
    sendEvent(name: "pm10DisplayValue", displayed: false, value: "${currentAQI.pm10}\n(${values.pm10} ug/m3)")
    sendEvent(name: "co2DisplayValue", displayed: false, value: "${currentAQI.co2}\n(${values.co2} ppm)")
    sendEvent(name: "hourlyPm25DisplayValue", displayed: false, value: "${hourlyAQI.pm25}\n(${values.hourlypm25} ug/m3)")
    sendEvent(name: "hourlyPm10DisplayValue", displayed: false, value: "${hourlyAQI.pm10}\n(${values.hourlypm10} ug/m3)")
    sendEvent(name: "hourlyCo2DisplayValue", displayed: false, value: "${hourlyAQI.co2}\n(${values.hourlyco2} ppm)")
}

def cToF(deg) {
	return round1(deg * (9.0/5.0) + 32)
}

def rangeAQI() { [  [1, 50],  [51, 100],   [101, 150],   [151, 200],    [201, 300],     [301, 400], [401, 500] ] }
def rangePM25() { [ [0, 12],  [12, 35.4],  [35.5, 55.4], [55.5, 150.4], [150.5, 250.4], [250.5, 350.4], [350.5, 500.4] ] }
def rangePM10() { [ [0, 54],  [55, 154],   [155, 254],   [255, 354],    [355, 424],     [425, 504], [505, 604] ] }
def rangeCO2() {  [ [0, 700], [701, 1000], [1001, 1500], [1501, 2500],  [2501, 5000] ] }

int calcAQI(c, ranges) {
	def idx = ranges.findIndexOf { it ->
    	c >= it[0] && c <= it[1]
    }
    def AQI = rangeAQI()
    def iHigh = AQI[idx][1]
    def iLow = AQI[idx][0]
    def cHigh = ranges[idx][1]
    def cLow = ranges[idx][0]
	return Math.round(((iHigh - iLow) / (cHigh - cLow))*(c - cLow) + iLow)
}

def round1(bigD) {
	return new BigDecimal(bigD).setScale(1, BigDecimal.ROUND_HALF_UP)
}

def currentAQI(d) {
	return [
        pm25: calcAQI(round1(d.current.p2), rangePM25()),
        pm10: calcAQI(Math.round(d.current.p1), rangePM10()),
        co2: calcAQI(Math.round(d.current.co), rangeCO2())
    ]
}

def hourlyAQI(d) {
	def p25 =  d.historical.hourly[0].p2_sum / d.historical.hourly[0].p2_count
    def p10 = d.historical.hourly[0].p1_sum / d.historical.hourly[0].p1_count
    def co = d.historical.hourly[0].co_sum / d.historical.hourly[0].co_count
    return [
    	pm25: calcAQI(round1(p25), rangePM25()),
    	pm10: calcAQI(Math.round(p10), rangePM10()),
    	co2: Math.round(calcAQI(Math.round(co), rangeCO2()))
    ]
}

def aqiDescription(aqis) {
	if (aqis.pm25 >= aqis.co2) {
    	return "PM2.5: ${aqis.pm25}"
    } else {
    	return "AQI: CO2: ${aqis.co2} (PM2.5: ${aqis.pm25}"
    }
}