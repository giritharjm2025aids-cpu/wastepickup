// ============================================================
// WastePickup - Client Side Controller
// Seamless REST API Integration with Spring Boot
// ============================================================

const API = "/api";

// Global In-Memory Stores
let zones = [];
let households = [];
let schedules = [];
let pickups = [];
let zoneScores = {}; // Map of zoneId -> average score

// Current Edit Targets (null if adding new)
let editZoneId = null;
let editScheduleId = null;
let editHouseholdId = null;
let editPickupId = null;

// ============================================================
// Initialization & Tab Navigation
// ============================================================
document.addEventListener("DOMContentLoaded", () => {
    setupTabNavigation();
    setupFormListeners();
    setupSearchFilters();
    setupDropdownCascades();

    // Initial Data Fetch
    loadAllData();
});

function setupTabNavigation() {
    const tabs = document.querySelectorAll(".nav-tab");
    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            const target = tab.getAttribute("data-tab");

            // Update Tab Buttons
            tabs.forEach(t => t.classList.remove("active"));
            tab.classList.add("active");

            // Update Tab Sections
            document.querySelectorAll(".tab-content").forEach(sec => {
                sec.classList.remove("active");
            });
            const activeSection = document.getElementById(`tab-${target}`);
            if (activeSection) {
                activeSection.classList.add("active");
            }

            // Hide success alerts if any when navigating
            const alertSuccess = document.getElementById("pickup-alert-success");
            if (alertSuccess) alertSuccess.classList.add("hidden");
        });
    });

    document.getElementById("btn-refresh-dashboard").addEventListener("click", () => {
        loadAllData();
    });
}

// ============================================================
// Master Data Loader
// ============================================================
async function loadAllData() {
    try {
        await Promise.all([
            fetchZones(),
            fetchHouseholds(),
            fetchSchedules(),
            fetchPickups()
        ]);

        // After all data is retrieved, fetch average scores for zones
        await fetchZoneAverageScores();

        // Render everything
        renderDashboard();
        renderZonesTable();
        renderSchedulesTable();
        renderHouseholdsTable();
        renderPickupsTable();

        // Populate Form Select Dropdowns
        updateDropdowns();
    } catch (err) {
        console.error("Error loading application data:", err);
    }
}

// ============================================================
// API Fetchers
// ============================================================
async function fetchZones() {
    try {
        const res = await fetch(`${API}/zones`);
        if (res.ok) zones = await res.json();
    } catch (e) { console.error("Error fetching zones", e); }
}

async function fetchHouseholds() {
    try {
        const res = await fetch(`${API}/households`);
        if (res.ok) households = await res.json();
    } catch (e) { console.error("Error fetching households", e); }
}

async function fetchSchedules() {
    try {
        const res = await fetch(`${API}/schedules`);
        if (res.ok) schedules = await res.json();
    } catch (e) { console.error("Error fetching schedules", e); }
}

async function fetchPickups() {
    try {
        const res = await fetch(`${API}/pickups`);
        if (res.ok) pickups = await res.json();
    } catch (e) { console.error("Error fetching pickups", e); }
}

async function fetchZoneAverageScores() {
    zoneScores = {};
    for (const z of zones) {
        try {
            const res = await fetch(`${API}/zones/${z.id}/average-score`);
            if (res.ok) {
                const data = await res.json();
                zoneScores[z.id] = data.averageScore != null ? data.averageScore : 0;
            } else {
                zoneScores[z.id] = 0;
            }
        } catch (e) {
            zoneScores[z.id] = 0;
        }
    }
}

// ============================================================
// RENDER: Dashboard
// ============================================================
function renderDashboard() {
    // 1. KPI Counts
    document.getElementById("kpi-zones").textContent = zones.length;
    document.getElementById("kpi-households").textContent = households.length;
    document.getElementById("kpi-pickups").textContent = pickups.length;

    // Flagged households count
    const flaggedList = households.filter(h => h.status === "FLAGGED");
    document.getElementById("kpi-flagged").textContent = flaggedList.length;

    // 2. Zone Average Scores Table
    const zoneTable = document.getElementById("dash-zones-table");
    zoneTable.innerHTML = "";
    if (zones.length === 0) {
        zoneTable.innerHTML = `<tr class="empty-row"><td colspan="3">No zones registered yet.</td></tr>`;
    } else {
        zones.forEach(z => {
            const score = zoneScores[z.id] !== undefined ? zoneScores[z.id] : 0;
            const scoreFormatted = Number(score) === 0 ? "0" : Number(score).toFixed(1);
            const scoreClass = Number(score) === 0 ? "score-red" : "score-gold";

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="td-id">#${z.id}</td>
                <td>${escapeHtml(z.name)}</td>
                <td class="${scoreClass}">★ ${scoreFormatted}</td>
            `;
            zoneTable.appendChild(tr);
        });
    }

    // 3. Low-Score Flagged Households Table
    const flaggedTable = document.getElementById("dash-flagged-table");
    flaggedTable.innerHTML = "";
    if (flaggedList.length === 0) {
        flaggedTable.innerHTML = `<tr class="empty-row"><td colspan="5">No flagged households. All meeting thresholds!</td></tr>`;
    } else {
        flaggedList.forEach(h => {
            const tr = document.createElement("tr");
            const zoneName = h.zone ? h.zone.name : "-";
            const avg = h.averageScore != null ? h.averageScore : "N/A";
            tr.innerHTML = `
                <td class="td-bold">${escapeHtml(h.name)}</td>
                <td>${escapeHtml(zoneName)}</td>
                <td>${h.minimumScore != null ? h.minimumScore : 50}</td>
                <td class="score-red">${avg}</td>
                <td><span class="badge-flagged">FLAGGED</span></td>
            `;
            flaggedTable.appendChild(tr);
        });
    }
}

// ============================================================
// RENDER: Zones Table
// ============================================================
function renderZonesTable(filterText = "") {
    const tbody = document.getElementById("zones-table-body");
    tbody.innerHTML = "";

    const filtered = zones.filter(z =>
        (z.name || "").toLowerCase().includes(filterText.toLowerCase()) ||
        String(z.id).includes(filterText)
    );

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="4">No matching zones found.</td></tr>`;
        return;
    }

    filtered.forEach(z => {
        const score = zoneScores[z.id] !== undefined ? zoneScores[z.id] : 0;
        const scoreFormatted = Number(score) === 0 ? "0" : Number(score).toFixed(1);
        const scoreClass = Number(score) === 0 ? "score-red" : "score-gold";

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="td-id">#${z.id}</td>
            <td>${escapeHtml(z.name)}</td>
            <td class="${scoreClass}">★ ${scoreFormatted}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action-edit" onclick="startEditZone(${z.id})">Edit</button>
                    <button class="btn-action-delete" onclick="deleteZone(${z.id})">Delete</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ============================================================
// RENDER: Schedules Table
// ============================================================
function renderSchedulesTable(filterText = "") {
    const tbody = document.getElementById("schedules-table-body");
    tbody.innerHTML = "";

    const filtered = schedules.filter(s => {
        const zoneName = s.zone ? s.zone.name : "";
        const day = s.pickupDay || "";
        return zoneName.toLowerCase().includes(filterText.toLowerCase()) ||
               day.toLowerCase().includes(filterText.toLowerCase()) ||
               String(s.id).includes(filterText);
    });

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="6">No schedules found.</td></tr>`;
        return;
    }

    filtered.forEach(s => {
        const zoneName = s.zone ? s.zone.name : "-";
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="td-id">#${s.id}</td>
            <td>${escapeHtml(zoneName)}</td>
            <td><span class="badge-day">${escapeHtml(s.pickupDay || "")}</span></td>
            <td>${s.startTime ? s.startTime.substring(0, 5) : "--:--"}</td>
            <td>${s.endTime ? s.endTime.substring(0, 5) : "--:--"}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action-edit" onclick="startEditSchedule(${s.id})">Edit</button>
                    <button class="btn-action-delete" onclick="deleteSchedule(${s.id})">Delete</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ============================================================
// RENDER: Households Table
// ============================================================
function renderHouseholdsTable(filterText = "") {
    const tbody = document.getElementById("households-table-body");
    tbody.innerHTML = "";

    const filtered = households.filter(h =>
        (h.name || "").toLowerCase().includes(filterText.toLowerCase()) ||
        (h.address || "").toLowerCase().includes(filterText.toLowerCase()) ||
        (h.phone || "").includes(filterText) ||
        (h.zone && h.zone.name && h.zone.name.toLowerCase().includes(filterText.toLowerCase())) ||
        String(h.id).includes(filterText)
    );

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="9">No households found.</td></tr>`;
        return;
    }

    filtered.forEach(h => {
        const zoneName = h.zone ? h.zone.name : "-";
        const statusBadge = h.status === "FLAGGED"
            ? `<span class="badge-flagged">FLAGGED</span>`
            : `<span class="badge-normal">NORMAL</span>`;

        const avgScore = h.averageScore != null ? h.averageScore : "N/A";

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="td-id">#${h.id}</td>
            <td class="td-bold">${escapeHtml(h.name)}</td>
            <td>${escapeHtml(h.address)}</td>
            <td>${escapeHtml(h.phone || "-")}</td>
            <td>${escapeHtml(zoneName)}</td>
            <td>${h.minimumScore != null ? h.minimumScore : 50}</td>
            <td>${avgScore}</td>
            <td>${statusBadge}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action-edit" onclick="startEditHousehold(${h.id})">Edit</button>
                    <button class="btn-action-delete" onclick="deleteHousehold(${h.id})">Delete</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ============================================================
// RENDER: Pickup Logs Table
// ============================================================
function renderPickupsTable(filterText = "") {
    const tbody = document.getElementById("pickups-table-body");
    tbody.innerHTML = "";

    const filtered = pickups.filter(p => {
        const hhName = p.household ? p.household.name : "";
        const zoneName = p.household && p.household.zone ? p.household.zone.name : (p.schedule && p.schedule.zone ? p.schedule.zone.name : "");
        return hhName.toLowerCase().includes(filterText.toLowerCase()) ||
               zoneName.toLowerCase().includes(filterText.toLowerCase()) ||
               (p.pickupDate || "").includes(filterText) ||
               String(p.id).includes(filterText);
    });

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="8">No pickup logs recorded.</td></tr>`;
        return;
    }

    // Sort descending by ID or date
    const sorted = [...filtered].sort((a, b) => (b.id || 0) - (a.id || 0));

    sorted.forEach(p => {
        const hhName = p.household ? p.household.name : "-";
        const zoneName = (p.household && p.household.zone) ? p.household.zone.name : (p.schedule && p.schedule.zone ? p.schedule.zone.name : "-");
        const schedWindow = p.schedule
            ? `${p.schedule.pickupDay || ""} (${(p.schedule.startTime || "").substring(0, 5)}-${(p.schedule.endTime || "").substring(0, 5)})`
            : "-";

        const score = p.segregationScore != null ? p.segregationScore : 0;
        let scoreClass = "score-green";
        if (score < 50) {
            scoreClass = "score-red";
        } else if (score < 60) {
            scoreClass = "score-gold";
        }

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="td-id">#${p.id}</td>
            <td>${escapeHtml(hhName)}</td>
            <td>${escapeHtml(zoneName)}</td>
            <td>${escapeHtml(schedWindow)}</td>
            <td>${p.pickupDate || "-"}</td>
            <td>${p.pickupTime ? p.pickupTime.substring(0, 5) : "--:--"}</td>
            <td class="${scoreClass}">★ ${score}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action-edit" onclick="startEditPickup(${p.id})">Edit</button>
                    <button class="btn-action-delete" onclick="deletePickup(${p.id})">Delete</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ============================================================
// Form Dropdowns Population
// ============================================================
function updateDropdowns() {
    // 1. Schedule Zone Dropdown
    const schedZoneSel = document.getElementById("schedule-zone");
    const currentSchedZone = schedZoneSel.value;
    schedZoneSel.innerHTML = `<option value="">-- Choose Zone --</option>`;
    zones.forEach(z => {
        schedZoneSel.innerHTML += `<option value="${z.id}">${escapeHtml(z.name)}</option>`;
    });
    if (currentSchedZone) schedZoneSel.value = currentSchedZone;

    // 2. Household Zone Dropdown
    const hhZoneSel = document.getElementById("household-zone");
    const currentHhZone = hhZoneSel.value;
    hhZoneSel.innerHTML = `<option value="">-- Choose Zone --</option>`;
    zones.forEach(z => {
        hhZoneSel.innerHTML += `<option value="${z.id}">${escapeHtml(z.name)}</option>`;
    });
    if (currentHhZone) hhZoneSel.value = currentHhZone;

    // 3. Pickup Household Dropdown
    const pickupHhSel = document.getElementById("pickup-household");
    const currentPickupHh = pickupHhSel.value;
    pickupHhSel.innerHTML = `<option value="">-- Select Household --</option>`;
    households.forEach(h => {
        const zoneName = h.zone ? h.zone.name : "No Zone";
        pickupHhSel.innerHTML += `<option value="${h.id}">${escapeHtml(h.name)} (${escapeHtml(zoneName)})</option>`;
    });
    if (currentPickupHh) pickupHhSel.value = currentPickupHh;

    // 4. Update Pickup Schedule Dropdown according to selected household
    filterPickupScheduleDropdown();
}

function filterPickupScheduleDropdown() {
    const pickupHhSel = document.getElementById("pickup-household");
    const pickupSchedSel = document.getElementById("pickup-schedule");
    const selectedHhId = Number(pickupHhSel.value);

    pickupSchedSel.innerHTML = `<option value="">-- Select Schedule Window --</option>`;

    let matchingSchedules = schedules;
    if (selectedHhId) {
        const hh = households.find(h => h.id === selectedHhId);
        if (hh && hh.zone && hh.zone.id) {
            matchingSchedules = schedules.filter(s => s.zone && s.zone.id === hh.zone.id);
        }
    }

    matchingSchedules.forEach(s => {
        const zName = s.zone ? s.zone.name : "Zone";
        const start = s.startTime ? s.startTime.substring(0, 5) : "";
        const end = s.endTime ? s.endTime.substring(0, 5) : "";
        pickupSchedSel.innerHTML += `<option value="${s.id}" data-day="${s.pickupDay}" data-start="${start}" data-end="${end}">
            ${escapeHtml(zName)} - ${escapeHtml(s.pickupDay)} (${start}-${end})
        </option>`;
    });
}

function setupDropdownCascades() {
    const pickupHhSel = document.getElementById("pickup-household");
    const pickupSchedSel = document.getElementById("pickup-schedule");

    pickupHhSel.addEventListener("change", () => {
        filterPickupScheduleDropdown();
    });

    // When a schedule is chosen, pre-fill pickup date with nearest matching day & pickup time with start time
    pickupSchedSel.addEventListener("change", () => {
        const opt = pickupSchedSel.selectedOptions[0];
        if (opt && opt.dataset.day) {
            const targetDay = opt.dataset.day.toUpperCase();
            const dateInput = document.getElementById("pickup-date");
            const timeInput = document.getElementById("pickup-time");

            // If time is empty, preset to start time
            if (!timeInput.value && opt.dataset.start) {
                timeInput.value = opt.dataset.start;
            }

            // Set to closest date matching the targetDay
            const matchedDate = getNearestDayDate(targetDay);
            if (matchedDate) {
                dateInput.value = matchedDate;
            }
        }
    });
}

function getNearestDayDate(dayName) {
    const days = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
    const targetIdx = days.indexOf(dayName);
    if (targetIdx === -1) return null;

    const now = new Date();
    const currentIdx = now.getDay();
    let diff = targetIdx - currentIdx;
    if (diff > 0) diff -= 7; // Go to most recent occurrence
    const targetDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() + diff);

    const y = targetDate.getFullYear();
    const m = String(targetDate.getMonth() + 1).padStart(2, "0");
    const d = String(targetDate.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
}

// ============================================================
// Search Filters Setup
// ============================================================
function setupSearchFilters() {
    document.getElementById("search-zones").addEventListener("input", (e) => {
        renderZonesTable(e.target.value.trim());
    });

    document.getElementById("search-schedules").addEventListener("input", (e) => {
        renderSchedulesTable(e.target.value.trim());
    });

    document.getElementById("search-households").addEventListener("input", (e) => {
        renderHouseholdsTable(e.target.value.trim());
    });

    document.getElementById("search-pickups").addEventListener("input", (e) => {
        renderPickupsTable(e.target.value.trim());
    });
}

// ============================================================
// Form Event Handlers (Create & Update)
// ============================================================
function setupFormListeners() {
    // 1. ZONE FORM
    const zoneForm = document.getElementById("zone-form");
    const zoneCancelBtn = document.getElementById("zone-cancel-btn");

    zoneForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const name = document.getElementById("zone-name").value.trim();
        if (!name) return;

        const payload = { name };
        try {
            let res;
            if (editZoneId) {
                res = await fetch(`${API}/zones/${editZoneId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                res = await fetch(`${API}/zones`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                alert(err.message || "Failed to save zone.");
                return;
            }

            resetZoneForm();
            await loadAllData();
        } catch (err) {
            alert("Error saving zone: " + err.message);
        }
    });

    zoneCancelBtn.addEventListener("click", resetZoneForm);

    // 2. SCHEDULE FORM
    const schedForm = document.getElementById("schedule-form");
    const schedCancelBtn = document.getElementById("schedule-cancel-btn");

    schedForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const zoneId = document.getElementById("schedule-zone").value;
        const pickupDay = document.getElementById("schedule-day").value;
        const startTime = document.getElementById("schedule-start").value;
        const endTime = document.getElementById("schedule-end").value;

        if (!zoneId || !pickupDay || !startTime || !endTime) {
            alert("Please fill in all schedule fields.");
            return;
        }

        const payload = {
            zone: { id: Number(zoneId) },
            pickupDay: pickupDay,
            startTime: startTime,
            endTime: endTime
        };

        try {
            let res;
            if (editScheduleId) {
                res = await fetch(`${API}/schedules/${editScheduleId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                res = await fetch(`${API}/schedules`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                alert(err.message || "Failed to save schedule.");
                return;
            }

            resetScheduleForm();
            await loadAllData();
        } catch (err) {
            alert("Error saving schedule: " + err.message);
        }
    });

    schedCancelBtn.addEventListener("click", resetScheduleForm);

    // 3. HOUSEHOLD FORM
    const hhForm = document.getElementById("household-form");
    const hhCancelBtn = document.getElementById("household-cancel-btn");

    hhForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const name = document.getElementById("household-name").value.trim();
        const address = document.getElementById("household-address").value.trim();
        const phone = document.getElementById("household-phone").value.trim();
        const zoneId = document.getElementById("household-zone").value;
        const minScore = document.getElementById("household-minscore").value;

        if (!name || !address || !phone || !zoneId) {
            alert("Please fill in all required household fields.");
            return;
        }

        const payload = {
            name: name,
            address: address,
            phone: phone,
            zone: { id: Number(zoneId) },
            minimumScore: minScore ? Number(minScore) : 50.0
        };

        try {
            let res;
            if (editHouseholdId) {
                res = await fetch(`${API}/households/${editHouseholdId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                res = await fetch(`${API}/households`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                alert(err.message || "Failed to save household.");
                return;
            }

            resetHouseholdForm();
            await loadAllData();
        } catch (err) {
            alert("Error saving household: " + err.message);
        }
    });

    hhCancelBtn.addEventListener("click", resetHouseholdForm);

    // 4. PICKUP LOG FORM
    const pickupForm = document.getElementById("pickup-form");
    const pickupCancelBtn = document.getElementById("pickup-cancel-btn");

    pickupForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const householdId = document.getElementById("pickup-household").value;
        const scheduleId = document.getElementById("pickup-schedule").value;
        const pickupDate = document.getElementById("pickup-date").value;
        const pickupTime = document.getElementById("pickup-time").value;
        const score = document.getElementById("pickup-score").value;

        if (!householdId || !scheduleId || !pickupDate || !pickupTime || score === "") {
            alert("Please fill in all required pickup log fields.");
            return;
        }

        const payload = {
            household: { id: Number(householdId) },
            schedule: { id: Number(scheduleId) },
            pickupDate: pickupDate,
            pickupTime: pickupTime,
            segregationScore: Number(score)
        };

        try {
            let res;
            if (editPickupId) {
                res = await fetch(`${API}/pickups/${editPickupId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            } else {
                res = await fetch(`${API}/pickups`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });
            }

            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                alert(err.message || "Failed to record pickup log.");
                return;
            }

            // Show Success Banner (matching screenshot)
            const alertBox = document.getElementById("pickup-alert-success");
            const alertText = document.getElementById("pickup-alert-text");
            alertText.textContent = `Waste pickup logged successfully! Score: ${score}`;
            alertBox.classList.remove("hidden");

            resetPickupForm(false);
            await loadAllData();
        } catch (err) {
            alert("Error recording pickup: " + err.message);
        }
    });

    pickupCancelBtn.addEventListener("click", () => resetPickupForm(true));
}

// ============================================================
// Form Reset & Cancel Helpers
// ============================================================
function resetZoneForm() {
    editZoneId = null;
    document.getElementById("zone-id").value = "";
    document.getElementById("zone-form").reset();
    document.getElementById("zone-form-title").textContent = "Add New Zone";
    document.getElementById("zone-submit-btn").textContent = "Save Zone";
    document.getElementById("zone-cancel-btn").classList.add("hidden");
}

function resetScheduleForm() {
    editScheduleId = null;
    document.getElementById("schedule-id").value = "";
    document.getElementById("schedule-form").reset();
    document.getElementById("schedule-form-title").textContent = "Add Collection Schedule";
    document.getElementById("schedule-submit-btn").textContent = "Save Schedule";
    document.getElementById("schedule-cancel-btn").classList.add("hidden");
}

function resetHouseholdForm() {
    editHouseholdId = null;
    document.getElementById("household-id").value = "";
    document.getElementById("household-form").reset();
    document.getElementById("household-minscore").value = "50";
    document.getElementById("household-form-title").textContent = "Add Household";
    document.getElementById("household-submit-btn").textContent = "Save Household";
    document.getElementById("household-cancel-btn").classList.add("hidden");
}

function resetPickupForm(hideAlert = true) {
    editPickupId = null;
    document.getElementById("pickup-id").value = "";
    document.getElementById("pickup-form").reset();
    document.getElementById("pickup-form-title").textContent = "Record Waste Pickup";
    document.getElementById("pickup-submit-btn").textContent = "Record Pickup";
    document.getElementById("pickup-cancel-btn").classList.add("hidden");
    filterPickupScheduleDropdown();

    if (hideAlert) {
        const alertBox = document.getElementById("pickup-alert-success");
        if (alertBox) alertBox.classList.add("hidden");
    }
}

// ============================================================
// Actions: Edit Handlers
// ============================================================
window.startEditZone = function(id) {
    const z = zones.find(item => item.id === id);
    if (!z) return;

    editZoneId = z.id;
    document.getElementById("zone-id").value = z.id;
    document.getElementById("zone-name").value = z.name || "";
    document.getElementById("zone-form-title").textContent = `Edit Zone (#${z.id})`;
    document.getElementById("zone-submit-btn").textContent = "Update Zone";
    document.getElementById("zone-cancel-btn").classList.remove("hidden");

    document.getElementById("zone-name").focus();
};

window.startEditSchedule = function(id) {
    const s = schedules.find(item => item.id === id);
    if (!s) return;

    editScheduleId = s.id;
    document.getElementById("schedule-id").value = s.id;
    document.getElementById("schedule-zone").value = s.zone ? s.zone.id : "";
    document.getElementById("schedule-day").value = s.pickupDay || "";
    document.getElementById("schedule-start").value = s.startTime ? s.startTime.substring(0, 5) : "";
    document.getElementById("schedule-end").value = s.endTime ? s.endTime.substring(0, 5) : "";

    document.getElementById("schedule-form-title").textContent = `Edit Schedule (#${s.id})`;
    document.getElementById("schedule-submit-btn").textContent = "Update Schedule";
    document.getElementById("schedule-cancel-btn").classList.remove("hidden");
};

window.startEditHousehold = function(id) {
    const h = households.find(item => item.id === id);
    if (!h) return;

    editHouseholdId = h.id;
    document.getElementById("household-id").value = h.id;
    document.getElementById("household-name").value = h.name || "";
    document.getElementById("household-address").value = h.address || "";
    document.getElementById("household-phone").value = h.phone || "";
    document.getElementById("household-zone").value = h.zone ? h.zone.id : "";
    document.getElementById("household-minscore").value = h.minimumScore != null ? h.minimumScore : 50;

    document.getElementById("household-form-title").textContent = `Edit Household (#${h.id})`;
    document.getElementById("household-submit-btn").textContent = "Update Household";
    document.getElementById("household-cancel-btn").classList.remove("hidden");

    document.getElementById("household-name").focus();
};

window.startEditPickup = function(id) {
    const p = pickups.find(item => item.id === id);
    if (!p) return;

    editPickupId = p.id;
    document.getElementById("pickup-id").value = p.id;
    document.getElementById("pickup-household").value = p.household ? p.household.id : "";

    filterPickupScheduleDropdown();

    document.getElementById("pickup-schedule").value = p.schedule ? p.schedule.id : "";
    document.getElementById("pickup-date").value = p.pickupDate || "";
    document.getElementById("pickup-time").value = p.pickupTime ? p.pickupTime.substring(0, 5) : "";
    document.getElementById("pickup-score").value = p.segregationScore != null ? p.segregationScore : "";

    document.getElementById("pickup-form-title").textContent = `Edit Pickup Log (#${p.id})`;
    document.getElementById("pickup-submit-btn").textContent = "Update Pickup";
    document.getElementById("pickup-cancel-btn").classList.remove("hidden");

    const alertBox = document.getElementById("pickup-alert-success");
    if (alertBox) alertBox.classList.add("hidden");
};

// ============================================================
// Actions: Delete Handlers
// ============================================================
window.deleteZone = async function(id) {
    if (!confirm("Are you sure you want to delete this zone? Schedules and households linked to it may be affected.")) {
        return;
    }
    try {
        const res = await fetch(`${API}/zones/${id}`, { method: "DELETE" });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            alert(err.message || "Failed to delete zone.");
            return;
        }
        if (editZoneId === id) resetZoneForm();
        await loadAllData();
    } catch (err) {
        alert("Error deleting zone: " + err.message);
    }
};

window.deleteSchedule = async function(id) {
    if (!confirm("Are you sure you want to delete this collection schedule?")) {
        return;
    }
    try {
        const res = await fetch(`${API}/schedules/${id}`, { method: "DELETE" });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            alert(err.message || "Failed to delete schedule.");
            return;
        }
        if (editScheduleId === id) resetScheduleForm();
        await loadAllData();
    } catch (err) {
        alert("Error deleting schedule: " + err.message);
    }
};

window.deleteHousehold = async function(id) {
    if (!confirm("Are you sure you want to delete this household?")) {
        return;
    }
    try {
        const res = await fetch(`${API}/households/${id}`, { method: "DELETE" });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            alert(err.message || "Failed to delete household.");
            return;
        }
        if (editHouseholdId === id) resetHouseholdForm();
        await loadAllData();
    } catch (err) {
        alert("Error deleting household: " + err.message);
    }
};

window.deletePickup = async function(id) {
    if (!confirm("Are you sure you want to delete this pickup log?")) {
        return;
    }
    try {
        const res = await fetch(`${API}/pickups/${id}`, { method: "DELETE" });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            alert(err.message || "Failed to delete pickup log.");
            return;
        }
        if (editPickupId === id) resetPickupForm(true);
        await loadAllData();
    } catch (err) {
        alert("Error deleting pickup log: " + err.message);
    }
};

// ============================================================
// Utilities
// ============================================================
function escapeHtml(str) {
    if (!str) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}