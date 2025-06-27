package com.shanudevcodes.newsbits.data

import com.shanudevcodes.newsbits.R

data class News(
    val id: Int,
    val imageResId: Int,
    val contentDescription: String,
    val writer: String,
    val timeAgo: String,
    val headline: String,
    val Content: String
)

val NewsList = listOf(
    News(
        id = 1,
        imageResId = R.drawable.img_1,
        contentDescription = "Smart city at night",
        writer = "Amal Jos Chacko",
        timeAgo = "5 hours ago",
        headline = "Crystals dancing to the tune of light might replace batteries",
        Content = """
            In a breakthrough that could redefine energy storage, researchers have engineered crystals that respond to specific light frequencies, effectively storing and releasing energy on demand. These new materials, called photonic-reactive crystals, vibrate at molecular levels when exposed to light, mimicking the electrochemical properties of traditional batteries without requiring rare-earth metals or chemical degradation.

            Unlike lithium-ion batteries, which degrade over time and pose environmental hazards, these crystals exhibit a renewable capacity for energy retention and emission. Imagine wearable devices, sensors, or even household electronics powered entirely by ambient light. The implications stretch beyond green tech — into space exploration, where consistent recharging is problematic, or in under-resourced areas where battery waste is a concern.

            According to the lead scientist, the next step involves scaling the technology and integrating it into existing electronic infrastructure. Several companies have already expressed interest in commercial applications, particularly in the IoT and smart wearables space. If successful, this could be the beginning of a new era in clean, efficient, and non-invasive energy storage.
        """.trimIndent()
    ),
    News(
        id = 2,
        imageResId = R.drawable.img_2,
        contentDescription = "AI revolution",
        writer = "Riya Thomas",
        timeAgo = "3 hours ago",
        headline = "AI learns to compose music indistinguishable from Mozart",
        Content = """
            A deep learning model developed by a team of musicologists and AI engineers has stunned experts by generating compositions nearly identical in structure and style to Mozart’s original works. Using a dataset of over 15,000 classical pieces, the AI was trained to recognize and replicate complex harmonic patterns, melodic contours, and even historical performance tendencies.

            The compositions produced were tested in blind evaluations against real Mozart pieces, with over 65% of listeners unable to distinguish the AI-generated music from authentic 18th-century work. Critics are calling it both a technological marvel and a philosophical puzzle. If machines can replicate human genius, where do we draw the line between creativity and computation?

            The AI system is now being proposed for use in film scoring, music therapy, and education. Some conservatories even see it as a tool for students to experiment with classical techniques. However, the move has sparked debate among traditionalists, who fear AI could overshadow human artistry.

            Whether a complement to human talent or a challenge to its uniqueness, one thing is clear — music creation will never sound the same again.
        """.trimIndent()
    ),
    News(
        id = 3,
        imageResId = R.drawable.img_3,
        contentDescription = "Mars landscape",
        writer = "Dr. Arun Mehta",
        timeAgo = "2 days ago",
        headline = "NASA discovers water activity under Martian ice",
        Content = """
            In a remarkable advancement in space science, NASA’s Mars Reconnaissance Orbiter has detected signs of liquid water movement beneath polar ice caps on the Red Planet. Utilizing radar technology, the orbiter identified thermal anomalies and structural shifts that are consistent with subterranean brine flow, pointing to the possible presence of salty liquid reservoirs.

            Scientists believe that these underground flows may provide stable conditions for microbial life. Unlike the harsh surface climate, which is exposed to extreme temperatures and radiation, subsurface environments might be shielded and chemically rich. This discovery reignites the long-debated question: Could Mars have once supported life — or does it still?

            The data also has implications for future human colonization. If harnessed effectively, this hidden water source could provide a vital supply of drinking water, as well as a base for fuel production using hydrogen electrolysis. NASA plans to conduct deeper scans using future missions like the Mars Ice Mapper, potentially paving the way for robotic excavation.

            The Red Planet continues to reveal secrets buried deep beneath its dusty surface — secrets that may change our understanding of life in the universe.
        """.trimIndent()
    ),
    News(
        id = 4,
        imageResId = R.drawable.img_4,
        contentDescription = "Ocean energy",
        writer = "Leena George",
        timeAgo = "1 hour ago",
        headline = "Ocean waves turned into electricity with new tech",
        Content = """
            A cutting-edge marine technology startup has unveiled a floating platform capable of converting the kinetic energy of ocean waves into usable electricity. The innovation, dubbed “AquaVolt,” uses a series of underwater rotors and flexible turbines that move in sync with wave patterns to generate power without harming marine ecosystems.

            Tested off the coast of Portugal, the prototype was able to generate a consistent output of 500 kW over a 24-hour period — enough to power an entire seaside village. Unlike wind or solar, wave energy offers a more predictable and constant energy source, making it highly attractive for grid stability.

            Environmentalists have praised the system for its minimal ecological footprint and potential to decentralize energy infrastructure in island nations and coastal towns. The team is now in talks with governments in Asia and Europe to scale up production.

            If adopted widely, ocean-powered grids could reduce global reliance on fossil fuels, especially in developing countries. The sea, it seems, may hold the key to clean energy independence.
        """.trimIndent()
    ),
    News(
        id = 5,
        imageResId = R.drawable.img_5,
        contentDescription = "Space telescope",
        writer = "Vinod Paul",
        timeAgo = "6 hours ago",
        headline = "Next-gen telescope captures galaxies born 13B years ago",
        Content = """
            The Jameson Ultra-Deep Space Telescope (JUDST) has captured the most detailed images yet of galaxies that formed just 300 million years after the Big Bang. This unprecedented clarity is credited to its advanced lensing systems and its position beyond Earth's atmospheric distortions in a deep space orbit.

            The findings, published in *Nature Astronomy*, reveal galaxy clusters so ancient that they challenge existing models of cosmic formation. Some show signs of complex structure, including starburst regions and primitive spiral formations, suggesting early galactic maturity.

            Astrophysicists are hailing this as a game-changer. The telescope’s data could refine our understanding of dark matter, the expansion rate of the universe, and the origins of cosmic background radiation. Moreover, the imagery offers hints of chemical elements — like carbon and oxygen — far earlier in cosmic history than previously believed.

            These revelations are fueling renewed enthusiasm for the search for extraterrestrial life and the study of cosmic evolution. The past, as it turns out, is written in the stars — and we’re just beginning to read it.
        """.trimIndent()
    ),
    News(
        id = 6,
        imageResId = R.drawable.img_6,
        contentDescription = "Tech startup",
        writer = "Sneha Nair",
        timeAgo = "20 minutes ago",
        headline = "Indian startup builds solar drones for rural delivery",
        Content = """
            An innovative Indian startup, SkyServe, has developed solar-powered drones capable of delivering supplies to rural and hard-to-reach communities. Designed with extended-range wings and lightweight solar panels, these drones can fly for up to 10 hours without recharging, making them ideal for remote delivery logistics.

            Initial trials conducted in villages of Maharashtra and Himachal Pradesh have shown remarkable results. From delivering medicines and food packets to academic materials and vaccines, the drones performed with precision and reliability. Unlike traditional delivery methods, which rely on inconsistent infrastructure, SkyServe offers a direct, eco-friendly route.

            Government bodies and NGOs are exploring partnerships to deploy the drones across disaster-prone and healthcare-deprived zones. The company also envisions future use in e-commerce and last-mile connectivity.

            With global climate concerns and infrastructural challenges mounting, SkyServe’s solar drone fleet could be a model not just for India — but for sustainable logistics worldwide.
        """.trimIndent()
    ),
    News(
        id = 7,
        imageResId = R.drawable.img_7,
        contentDescription = "Quantum chips",
        writer = "Ajay Menon",
        timeAgo = "8 hours ago",
        headline = "Quantum chips now tested in commercial smartphones",
        Content = """
            In a technological leap, a collaborative research effort has resulted in the integration of quantum processing units (QPUs) into prototype commercial smartphones. While traditional CPUs handle binary computation, QPUs use quantum bits to perform massively parallel operations — drastically reducing computational time for complex tasks.

            The test devices, distributed among developers and researchers, showed enhanced performance in cryptographic functions, AI image recognition, and predictive modeling. These early quantum chips are not replacing current processors but instead work as co-processors, offloading certain calculations where quantum advantages shine.

            Privacy advocates and developers are thrilled about what this means for on-device encryption, as quantum cryptography could become native. However, challenges remain in terms of heat dissipation and battery consumption, which researchers aim to resolve in upcoming iterations.

            With quantum computing transitioning from lab-scale to pocket-sized, we may be entering an era where the average smartphone could rival supercomputers of just a decade ago. Welcome to the age of quantum mobility.
        """.trimIndent()
    )
)