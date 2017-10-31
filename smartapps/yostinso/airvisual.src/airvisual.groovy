/**
 *  AirVisual
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
definition(
    name: "AirVisual",
    namespace: "yostinso",
    author: "E.O. Stinson",
    description: "Add any number of AirVisual Node Pro devices to your SmartThings network.",
    category: "Health & Wellness",
    iconUrl: "https://d25jl8yaav4s0u.cloudfront.net/images/app/health-recommendations-icon.png",
    singleInstance: true
)


preferences {
	page(name: "mainPage", title: "Nodes", install: true, uninstall: true) {
		section {
        	app(name: "nodeApps", appName: "AirVisual Node", namespace: "yostinso", title: "Add a Node", multiple: true)
		}
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}