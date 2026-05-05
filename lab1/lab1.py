import random

# Number of records to generate for each file (change these constants as needed)
NUM_STORES    = 100
NUM_CUSTOMERS = 1000
NUM_SALES     = 2000
NUM_PRODUCTS  = 100
NUM_LINE_ITEMS = 4000

#----
# From wikipedia - list of US department/retail store chains
store_names = [
    "JCPenney",
    "Kohl's",
    "Bloomingdale's",
    "Macy's",
    "Nordstrom",
    "Nordstrom Rack",
    "Neiman Marcus",
    "Saks Fifth Avenue",
    "Saks Off 5th",
    "Kmart",
    "Sears",
    "Target",
    "Walmart"
]

#----
# From geographic.org list of street names in Orcutt, CA
street_names = [
    "Airport Rd",
    "Alderberry Dr",
    "Apple Grove Ln",
    "Atherly Ln",
    "Atterdag Rd",
    "Blanche Ct",
    "Bradford Dr",
    "Bradley",
    "Bramadero Rd",
    "Brooks St",
    "Bush Ct",
    "Cagney Way",
    "Caleta Ave",
    "Calle De La Montana",
    "Canyon Acres Dr",
    "Carlotti Dr",
    "Cedar Vista",
    "Crestmont Ct",
    "Danbury Ct",
    "Dariesa St",
    "Date St",
    "Del Cielo Ct",
    "Delaguera St",
    "Dermanak Dr",
    "Dickinson St",
    "Don Pablo Dr",
    "Dorset Ct",
    "Drummer Ct",
    "Dublin Ct",
    "Duvali Dr",
    "E Victoria St",
    "Edison Ave",
    "El Pasillo",
    "Elder",
    "Elk Grove Rd",
    "Fair Oaks Dr",
    "Fortunato Way",
    "Friendship Ln",
    "Gemini St",
    "Glen Ellen Ln",
    "Glen Ln",
    "Glenview Rd",
    "Greenwell Ln",
    "Halkirk St",
    "Harmony Ln",
    "Haslam Dr",
    "Hastings Dr",
    "Hillview Dr",
    "Holly Ln",
    "Humbolt Dr",
    "Hummel Dr",
    "Hummel Village Cir",
    "Ken Ave",
    "Kenmore Pl",
    "Lakeview Ct",
    "Lantana Ct",
    "Lemonwood Dr",
    "Luneta Plaza",
    "Lupine St",
    "Magnolia St",
    "Marcia Way",
    "May Ct",
    "Miguelito Rd",
    "Montano Dr",
    "N Broadway",
    "N G St",
    "N Ontare Rd",
    "Noroeste Ave",
    "Norris St",
    "Northoaks Dr",
    "Oak Ave",
    "Oak Hurst Ct",
    "Oak Park Ln",
    "Odense Dr",
    "Pagaling Dr",
    "Palomar Cir",
    "Parkhurst Dr",
    "Parkside Way",
    "Parkview Rd",
    "Pimiento Ln",
    "Princeton Ave",
    "Quail Ct",
    "Quail Ridge Rd",
    "Racquet Club Dr",
    "Ray Rd",
    "Redbird Ct",
    "Richview Rd",
    "Romero Canyon Rd",
    "S 1st Pl",
    "S Blosser Rd",
    "S Broadway",
    "S G St",
    "S P St",
    "Sagan Ct",
    "Shady Glade Dr",
    "Snowy Plover Ln",
    "Spruce Dr",
    "St George Pl",
    "Still Meadow Rd",
    "Stillwell",
    "Stratford Pl",
    "Stuart Dr",
    "Sweet Rain Pl",
    "Tallant Rd",
    "Terrace Vista Ln",
    "Thompson Way",
    "Tierra Bella",
    "Trudy Ct",
    "Twinridge Rd",
    "Umbra Rd"
]

#----
# Paired city/state so they always match
states = [
    "CA", "TX", "FL", "NY", "IL",
    "AZ", "CO", "GA", "WA", "MA"
]

cities = [
    "Sacramento",   # California
    "Austin",       # Texas
    "Tallahassee",  # Florida
    "Albany",       # New York
    "Springfield",  # Illinois
    "Phoenix",      # Arizona
    "Denver",       # Colorado
    "Atlanta",      # Georgia
    "Olympia",      # Washington
    "Boston"        # Massachusetts
]

#----
# From census.gov lists of common first and last names
first_names = [
    "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael",
    "Linda", "William", "Barbara", "David", "Elizabeth", "Richard", "Susan",
    "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen", "Christopher",
    "Lisa", "Daniel", "Nancy", "Matthew", "Betty", "Anthony", "Margaret",
    "Mark", "Sandra", "Donald", "Ashley", "Steven", "Dorothy", "Paul",
    "Kimberly", "Andrew", "Emily", "Kenneth", "Donna", "Joshua", "Michelle",
    "Kevin", "Carol", "Brian", "Amanda", "George", "Melissa", "Edward",
    "Deborah", "Ronald", "Stephanie", "Timothy", "Rebecca", "Jason", "Sharon"
]

last_names = [
    "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
    "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
    "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
    "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark",
    "Ramirez", "Lewis", "Robinson", "Walker", "Young", "Allen", "King",
    "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green",
    "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
    "Carter", "Roberts", "Phillips", "Evans", "Turner", "Diaz", "Parker"
]

#----
# Products with realistic descriptions and prices
products = [
    ("iPhone 15 Pro", 999.99),
    ("Samsung Galaxy S24", 849.99),
    ("Google Pixel 8", 699.99),
    ("iPad Air 11in", 599.99),
    ("iPad Mini", 499.99),
    ("MacBook Air M3", 1099.99),
    ("MacBook Pro 14in", 1999.99),
    ("Dell XPS 13", 1299.99),
    ("HP Spectre x360", 1149.99),
    ("Lenovo ThinkPad X1", 1399.99),
    ("Microsoft Surface Pro", 1099.99),
    ("Apple Watch Series 9", 399.99),
    ("Samsung Galaxy Watch 6", 299.99),
    ("Fitbit Charge 6", 159.99),
    ("Garmin Forerunner 265", 449.99),
    ("Sony WH-1000XM5", 349.99),
    ("Bose QuietComfort 45", 329.99),
    ("AirPods Pro 2nd Gen", 249.99),
    ("Jabra Elite 85t", 179.99),
    ("Beats Studio Pro", 349.99),
    ("Amazon Echo Dot", 49.99),
    ("Google Nest Hub", 99.99),
    ("Apple HomePod Mini", 99.99),
    ("Roku Streaming Stick 4K", 49.99),
    ("Amazon Fire TV Stick 4K", 39.99),
    ("Nintendo Switch OLED", 349.99),
    ("PlayStation 5", 499.99),
    ("Xbox Series X", 499.99),
    ("Steam Deck 512GB", 649.99),
    ("Oculus Quest 3", 499.99),
    ("Canon EOS R50", 679.99),
    ("Sony Alpha a6700", 1399.99),
    ("Nikon Z30", 799.99),
    ("GoPro Hero 12", 399.99),
    ("DJI Mini 4 Pro", 759.99),
    ("Ring Video Doorbell", 99.99),
    ("Nest Learning Thermostat", 249.99),
    ("Philips Hue Starter Kit", 179.99),
    ("TP-Link WiFi 6 Router", 129.99),
    ("Netgear Nighthawk", 199.99),
    ("Samsung 55in QLED TV", 799.99),
    ("LG C3 65in OLED TV", 1799.99),
    ("Sony Bravia 75in", 1499.99),
    ("TCL 50in 4K TV", 349.99),
    ("Hisense 43in 4K TV", 249.99),
    ("Epson EcoTank ET-4850", 349.99),
    ("HP OfficeJet Pro 9015e", 229.99),
    ("Canon PIXMA TR8620", 149.99),
    ("Brother MFC-L8900CDW", 549.99),
    ("Western Digital 4TB HDD", 89.99),
    ("Seagate 2TB Portable", 59.99),
    ("Samsung 1TB SSD", 89.99),
    ("WD Black SN850X 2TB", 149.99),
    ("Corsair Vengeance 32GB RAM", 74.99),
    ("Crucial 16GB DDR5", 49.99),
    ("ASUS ROG Gaming Mouse", 79.99),
    ("Logitech MX Master 3", 99.99),
    ("Razer BlackWidow Keyboard", 139.99),
    ("Corsair K95 Keyboard", 199.99),
    ("BenQ 27in 4K Monitor", 599.99),
    ("LG 32in UltraWide", 449.99),
    ("Dell 27in Gaming Monitor", 379.99),
    ("Asus ROG Swift 32in", 699.99),
    ("Anker 20000mAh Power Bank", 45.99),
    ("Belkin MagSafe Charger", 39.99),
    ("USB-C Hub 7-in-1", 29.99),
    ("Amazon Basics HDMI Cable", 9.99),
    ("Surge Protector 12-Outlet", 34.99),
    ("APC UPS 1500VA", 229.99),
    ("Dyson V15 Vacuum", 749.99),
    ("iRobot Roomba j7+", 599.99),
    ("Shark IQ Robot", 399.99),
    ("Bissell CrossWave", 279.99),
    ("Instant Pot Duo 7-in-1", 99.99),
    ("Ninja Foodi Air Fryer", 159.99),
    ("KitchenAid Stand Mixer", 449.99),
    ("Keurig K-Elite", 169.99),
    ("Breville Espresso Machine", 699.99),
    ("Vitamix E310", 449.99),
    ("Nespresso Vertuo Pop", 89.99),
    ("Cuisinart Food Processor", 199.99),
    ("Lodge Cast Iron Skillet", 34.99),
    ("All-Clad Stainless Set", 699.99),
    ("Hydro Flask 32oz", 44.95),
    ("Yeti Rambler 20oz", 34.99),
    ("Fossil Gen 6 Smartwatch", 299.99),
    ("Tile Mate 4-Pack", 59.99),
    ("Theragun Prime", 299.99),
    ("Peloton Bike+", 2495.00),
    ("Bowflex SelectTech 552", 449.99),
    ("Resistance Bands Set", 24.99),
    ("Yoga Mat Premium", 79.99),
    ("Jump Rope Speed", 14.99),
    ("Lego Technic Set", 129.99),
    ("Monopoly Board Game", 19.99),
    ("Kindle Paperwhite 16GB", 159.99),
    ("Audible Gift Card", 25.00),
    ("Spotify Gift Card", 30.00),
    ("Netflix Gift Card", 50.00),
    ("Google Play Card", 25.00),
    ("iTunes Gift Card", 50.00),
]

#----
# Helper: random phone number
def rand_phone():
    return f"{random.randint(100, 999)} {random.randint(100, 999)} {random.randint(1000, 9999)}"

# Helper: random date between two years (returns YYYY/MM/DD)
def rand_date(start_year, end_year):
    y = random.randint(start_year, end_year)
    m = random.randint(1, 12)
    d = random.randint(1, 28)  # cap at 28 to avoid invalid dates
    return f"{y}/{m:02d}/{d:02d}"

# Helper: random time (returns HH:MM:SS)
def rand_time():
    h = random.randint(8, 21)
    m = random.randint(0, 59)
    s = random.randint(0, 59)
    return f"{h:02d}:{m:02d}:{s:02d}"

#----
# Generate store_data.txt
# Format: ID, storeName, address, city, ZIP, state, phoneNumber
with open("store_data.txt", "w") as f:
    for i in range(NUM_STORES):
        idx = random.randint(0, len(states) - 1)
        city = cities[idx]
        state = states[idx]

        street_num = random.randint(100, 9999)
        street = random.choice(street_names)
        address = f"{street_num} {street}"

        zip_code = random.randint(10000, 99999)  # format correct, not matched to city

        phone_number = rand_phone()
        store_name = random.choice(store_names)
        f.write(f"{i+1}, {store_name}, {address}, {city}, {zip_code}, {state}, {phone_number}\n")

print("store_data.txt done")

#----
# Generate customer_data.txt
# Format: ID, name, birthDate, address, city, ZIP, state, phoneNumber
with open("customer_data.txt", "w") as f:
    for i in range(NUM_CUSTOMERS):
        name = f"{random.choice(first_names)} {random.choice(last_names)}"
        dob = rand_date(1950, 2005)

        street_num = random.randint(100, 9999)
        street = random.choice(street_names)
        address = f"{street_num} {street}"

        idx = random.randint(0, len(states) - 1)
        city = cities[idx]
        state = states[idx]
        zip_code = random.randint(10000, 99999)

        phone_number = rand_phone()
        f.write(f"{i+1}, {name}, {dob}, {address}, {city}, {zip_code}, {state}, {phone_number}\n")

print("customer_data.txt done")

#----
# Generate sales_data.txt
# Format: ID, date, time, storeID, customerID
# Every store and every customer must appear in at least one sale
with open("sales_data.txt", "w") as f:
    store_ids    = list(range(1, NUM_STORES + 1))
    customer_ids = list(range(1, NUM_CUSTOMERS + 1))

    # Build mandatory pairings so every store and every customer has at least one sale
    mandatory = []
    random.shuffle(store_ids)
    random.shuffle(customer_ids)
    for s, c in zip(store_ids, customer_ids[:NUM_STORES]):         # covers all stores
        mandatory.append((s, c))
    for c in customer_ids[NUM_STORES:]:                            # covers remaining customers
        mandatory.append((random.choice(store_ids), c))

    # Fill the rest of the sales randomly
    extra_count = NUM_SALES - len(mandatory)
    extra = [(random.choice(store_ids), random.choice(customer_ids))
             for _ in range(extra_count)]

    all_sales = mandatory + extra
    random.shuffle(all_sales)

    for i, (sid, cid) in enumerate(all_sales):
        date = rand_date(2015, 2024)
        time = rand_time()
        f.write(f"{i+1}, {date}, {time}, {sid}, {cid}\n")

print("sales_data.txt done")

#----
# Generate product_data.txt
# Format: ID, description, price
with open("product_data.txt", "w") as f:
    for i in range(NUM_PRODUCTS):
        desc, price = products[i]
        f.write(f"{i+1}, {desc}, {price:.2f}\n")

print("product_data.txt done")

#----
# Generate line_item_data.txt
# Format: ID, salesID, productID, quantity
# Every sale must have at least one line item
with open("line_item_data.txt", "w") as f:
    sale_ids    = list(range(1, NUM_SALES + 1))
    product_ids = list(range(1, NUM_PRODUCTS + 1))

    # One mandatory line item per sale so every sale is covered
    mandatory = [(sid, random.choice(product_ids)) for sid in sale_ids]

    # Fill remaining line items randomly
    extra_count = NUM_LINE_ITEMS - len(mandatory)
    extra = [(random.choice(sale_ids), random.choice(product_ids))
             for _ in range(extra_count)]

    all_items = mandatory + extra
    random.shuffle(all_items)

    for i, (sid, pid) in enumerate(all_items):
        qty = random.randint(1, 10)
        f.write(f"{i+1}, {sid}, {pid}, {qty}\n")

print("line_item_data.txt done")
