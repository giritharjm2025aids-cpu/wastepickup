const API = "/api";

let zones = [];
let households = [];
let schedules = [];
let pickups = [];


// ===============================
// Load Data
// ===============================

async function loadData() {
    try {
        await loadZones();
        await loadHouseholds();
        await loadSchedules();
        await loadPickups();
        await loadFlaggedHouseholds();
        updateDashboard();
    } catch (error) {
        console.error("Error loading data:", error);
        alert("Unable to connect to the Spring Boot server.");
    }
}


// ===============================
// Zones
// ===============================

async function loadZones() {

    const response = await fetch(`${API}/zones`);

    if (!response.ok) {
        throw new Error("Failed to load zones");
    }

    zones = await response.json();

    displayZones();
    updateZoneDropdowns();
}


function displayZones() {

    const table = document.getElementById("zoneTableBody");

    table.innerHTML = "";

    zones.forEach(zone => {

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${zone.id}</td>
            <td>${zone.name}</td>
        `;

        table.appendChild(row);
    });
}


function updateZoneDropdowns() {

    const householdZone = document.getElementById("householdZone");
    const scheduleZone = document.getElementById("scheduleZone");

    householdZone.innerHTML =
        '<option value="">Select Zone</option>';

    scheduleZone.innerHTML =
        '<option value="">Select Zone</option>';

    zones.forEach(zone => {

        householdZone.innerHTML += `
            <option value="${zone.id}">
                ${zone.name}
            </option>
        `;

        scheduleZone.innerHTML += `
            <option value="${zone.id}">
                ${zone.name}
            </option>
        `;
    });
}


// Add Zone

document.getElementById("zoneForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const name = document.getElementById("zoneName").value.trim();

    if (!name) {
        alert("Please enter zone name.");
        return;
    }

    try {

        const response = await fetch(`${API}/zones`, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                name: name
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || "Failed to add zone");
        }

        alert("Zone added successfully!");

        document.getElementById("zoneForm").reset();

        await loadZones();

        updateDashboard();

    } catch (error) {

        alert(error.message);
    }
});


// ===============================
// Households
// ===============================

async function loadHouseholds() {

    const response = await fetch(`${API}/households`);

    if (!response.ok) {
        throw new Error("Failed to load households");
    }

    households = await response.json();

    displayHouseholds();
    updateHouseholdDropdown();
}


function displayHouseholds() {

    const table = document.getElementById("householdTableBody");

    table.innerHTML = "";

    households.forEach(household => {

        const row = document.createElement("tr");

        const status = household.flagged
            ? '<span class="status-flagged">Flagged</span>'
            : '<span class="status-good">Good</span>';

        row.innerHTML = `
            <td>${household.id}</td>
            <td>${household.householdName}</td>
            <td>${household.address}</td>
            <td>${household.zone ? household.zone.name : "-"}</td>
            <td>${status}</td>
        `;

        table.appendChild(row);
    });
}


function updateHouseholdDropdown() {

    const dropdown = document.getElementById("pickupHousehold");

    dropdown.innerHTML =
        '<option value="">Select Household</option>';

    households.forEach(household => {

        dropdown.innerHTML += `
            <option value="${household.id}">
                ${household.householdName}
            </option>
        `;
    });
}


// Add Household

document.getElementById("householdForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const householdName =
        document.getElementById("householdName").value.trim();

    const address =
        document.getElementById("address").value.trim();

    const zoneId =
        document.getElementById("householdZone").value;

    if (!householdName || !address || !zoneId) {

        alert("Please fill all household details.");

        return;
    }

    try {

        const response = await fetch(
            `${API}/households/${zoneId}`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    householdName: householdName,
                    address: address,
                    flagged: false
                })
            }
        );

        if (!response.ok) {

            const error = await response.json();

            throw new Error(
                error.error || "Failed to add household"
            );
        }

        alert("Household added successfully!");

        document.getElementById("householdForm").reset();

        await loadHouseholds();

        updateDashboard();

    } catch (error) {

        alert(error.message);
    }
});


// ===============================
// Schedules
// ===============================

async function loadSchedules() {

    const response = await fetch(`${API}/schedules`);

    if (!response.ok) {
        throw new Error("Failed to load schedules");
    }

    schedules = await response.json();

    displaySchedules();
}


function displaySchedules() {

    const table =
        document.getElementById("scheduleTableBody");

    table.innerHTML = "";

    schedules.forEach(schedule => {

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${schedule.id}</td>
            <td>${schedule.zone ? schedule.zone.name : "-"}</td>
            <td>${schedule.dayOfWeek}</td>
            <td>${schedule.startTime}</td>
            <td>${schedule.endTime}</td>
        `;

        table.appendChild(row);
    });
}


// Add Schedule

document.getElementById("scheduleForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const zoneId =
        document.getElementById("scheduleZone").value;

    const dayOfWeek =
        document.getElementById("dayOfWeek").value;

    const startTime =
        document.getElementById("startTime").value;

    const endTime =
        document.getElementById("endTime").value;

    if (!zoneId || !dayOfWeek || !startTime || !endTime) {

        alert("Please fill all schedule details.");

        return;
    }

    if (startTime >= endTime) {

        alert("End time must be after start time.");

        return;
    }

    try {

        const response = await fetch(
            `${API}/schedules/${zoneId}`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    dayOfWeek: dayOfWeek,
                    startTime: startTime + ":00",
                    endTime: endTime + ":00"
                })
            }
        );

        if (!response.ok) {

            const error = await response.json();

            throw new Error(
                error.error || "Failed to add schedule"
            );
        }

        alert("Schedule added successfully!");

        document.getElementById("scheduleForm").reset();

        await loadSchedules();

    } catch (error) {

        alert(error.message);
    }
});


// ===============================
// Pickup Logs
// ===============================

async function loadPickups() {

    const response = await fetch(`${API}/pickups`);

    if (!response.ok) {
        throw new Error("Failed to load pickup logs");
    }

    pickups = await response.json();

    displayPickups();
}


function displayPickups() {

    const table =
        document.getElementById("pickupTableBody");

    table.innerHTML = "";

    pickups.forEach(pickup => {

        const row = document.createElement("tr");

        const householdName =
            pickup.household
                ? pickup.household.householdName
                : "-";

        row.innerHTML = `
            <td>${pickup.id}</td>
            <td>${householdName}</td>
            <td>${formatDateTime(pickup.pickupTime)}</td>
            <td>${pickup.segregationScore}</td>
            <td>${pickup.collectorName || "-"}</td>
        `;

        table.appendChild(row);
    });
}


// Add Pickup

document.getElementById("pickupForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const householdId =
        document.getElementById("pickupHousehold").value;

    const pickupTime =
        document.getElementById("pickupTime").value;

    const score =
        document.getElementById("segregationScore").value;

    const collectorName =
        document.getElementById("collectorName").value.trim();

    if (!householdId || !pickupTime || score === "") {

        alert("Please fill all required pickup details.");

        return;
    }

    if (score < 0 || score > 100) {

        alert("Segregation score must be between 0 and 100.");

        return;
    }

    try {

        const response = await fetch(
            `${API}/pickups/${householdId}`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    pickupTime: pickupTime + ":00",
                    segregationScore: Number(score),
                    collectorName: collectorName
                })
            }
        );

        if (!response.ok) {

            const error = await response.json();

            throw new Error(
                error.error || "Failed to record pickup"
            );
        }

        alert("Pickup recorded successfully!");

        document.getElementById("pickupForm").reset();

        await loadHouseholds();
        await loadPickups();
        await loadFlaggedHouseholds();

        updateDashboard();

    } catch (error) {

        alert(error.message);
    }
});


// ===============================
// Flagged Households
// ===============================

async function loadFlaggedHouseholds() {

    const response =
        await fetch(`${API}/households/flagged`);

    if (!response.ok) {
        throw new Error("Failed to load flagged households");
    }

    const flagged =
        await response.json();

    displayFlaggedHouseholds(flagged);
}


function displayFlaggedHouseholds(flagged) {

    const table =
        document.getElementById("flaggedTableBody");

    table.innerHTML = "";

    flagged.forEach(household => {

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${household.id}</td>
            <td>${household.householdName}</td>
            <td>${household.address}</td>
            <td>${household.zone ? household.zone.name : "-"}</td>
            <td>
                <span class="status-flagged">
                    Flagged
                </span>
            </td>
        `;

        table.appendChild(row);
    });
}


// ===============================
// Dashboard
// ===============================

function updateDashboard() {

    document.getElementById("zoneCount").textContent =
        zones.length;

    document.getElementById("householdCount").textContent =
        households.length;

    document.getElementById("pickupCount").textContent =
        pickups.length;

    const flaggedCount =
        households.filter(household => household.flagged).length;

    document.getElementById("flaggedCount").textContent =
        flaggedCount;
}


// ===============================
// Date Formatting
// ===============================

function formatDateTime(dateTime) {

    if (!dateTime) {
        return "-";
    }

    const date = new Date(dateTime);

    if (isNaN(date.getTime())) {
        return dateTime;
    }

    return date.toLocaleString();
}


// ===============================
// Start Application
// ===============================

document.addEventListener("DOMContentLoaded", function() {

    loadData();

});