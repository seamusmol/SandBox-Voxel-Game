package serverlevelgen;

public class BiomeData {

	public static enum Plants
	{
		plant(0),
		otherPlant(1);
		
		private final int value;

        Plants(int newValue)
        {
            value = newValue;
        }
        
        public static String getName(int ID)
        {
        	for(int i = 0; i < Plants.values().length; i++)
        	{
        		if(Plants.values()[i].value == ID)
        		{
        			return Plants.values()[i].name();
        		}
        	}
        	return "";
        }
	}
	
	public static enum BiomePlants
	{
		Coast(0),
		SubTropicalDesert(0),
		SemiAridDesert(0),
		TemperateDesert(0),
		AridDesert(0),
		
		GrassSavanna(0),
		TreeSavanna(0),
		XericShrubland(0),
		DrySteppe(0),
		DesertSteppe(0),
		
		BroadLeafForest(0),
		DecidiousForest(0),
		Shrubland(0),
		ModerateSteppe(0),
		BarrenSteppe(0),
		
		SubTropicalRainForest(0),
		TemperateForest(0),
		Mediterranean(0),
		Tundra(0),
		AlpineTundra(0),
		
		TropicalRainForest(0),
		MonsoonForest(0),
		Taiga(0),
		MontaneForest(0),
		SnowPolarDesert(0);
		
		private final int[] value;

        BiomePlants(int... newValue)
        {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
	}
	
	public static enum BiomePlantDistribution
	{
		Coast(80),
		SubTropicalDesert(80),
		SemiAridDesert(80),
		TemperateDesert(80),
		AridDesert(80),
		
		GrassSavanna(80),
		TreeSavanna(80),
		XericShrubland(80),
		DrySteppe(80),
		DesertSteppe(80),
		
		BroadLeafForest(80),
		DecidiousForest(80),
		Shrubland(80),
		ModerateSteppe(80),
		BarrenSteppe(80),
		
		SubTropicalRainForest(80),
		TemperateForest(80),
		Mediterranean(80),
		Tundra(80),
		AlpineTundra(80),
		
		TropicalRainForest(80),
		MonsoonForest(80),
		Taiga(80),
		MontaneForest(80),
		SnowPolarDesert(80);
		
        private final int[] value;

        BiomePlantDistribution(int... newValue) {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
	}
	
	//Vegetation, Trees
	public static enum BiomeVegetationDistribution
	{
		Coast(1.0f,0),
		SubTropicalDesert(0.5f,0.5f),
		SemiAridDesert(0.5f,0.5f),
		TemperateDesert(0.5f,0.5f),
		AridDesert(0.5f,0.5f),
		
		GrassSavanna(0.5f,0.5f),
		TreeSavanna(0.5f,0.5f),
		XericShrubland(0.5f,0.5f),
		DrySteppe(0.5f,0.5f),
		DesertSteppe(0.5f,0.5f),
		
		BroadLeafForest(0.5f,0.5f),
		DecidiousForest(0.5f,0.5f),
		Shrubland(0.5f,0.5f),
		ModerateSteppe(0.5f,0.5f),
		BarrenSteppe(0.5f,0.5f),
		
		SubTropicalRainForest(0.5f,0.5f),
		TemperateForest(0.5f,0.5f),
		Mediterranean(0.5f,0.5f),
		Tundra(0.5f,0.5f),
		AlpineTundra(0.5f,0.5f),
		
		TropicalRainForest(0.5f,0.5f),
		MonsoonForest(0.5f,0.5f),
		Taiga(0.5f,0.5f),
		MontaneForest(0.5f,0.5f),
		SnowPolarDesert(0.5f,0.5f);
		
        private final float[] value;

        BiomeVegetationDistribution(float... newValue) {
            value = newValue;
        }

        public float[] getValue() 
        { 
        	return value; 
        }
	}
	
	//stores material names
	public static enum Material
	{
		Air(0), Dirt(1), Sand(2), Grass(3), Rock(4), OtherRock(5), TDB6(6), TDB7(7),
		TDB8(8), TDB9(9), TDB10(10), TDB11(11), TDB12(12), TDB13(13), TDB14(14), TDB15(15),
		TDB16(16), TDB17(17), TDB18(18), TDB19(19), TDB20(20), TDB21(21), TDB22(22),TDB23(23),
		TDB24(24), TDB25(25), TDB26(26), TDB27(27), TDB28(28), TDB29(29), TDB30(30), TDB31(31),
		TDB32(32), TDB33(33), TDB34(34), TDB35(35), TDB36(36),TDB37(37), TDB38(38), TDB39(39),
		TDB40(40), TDB41(41), TDB42(42), TDB43(43), TDB44(44), TDB45(45), TDB46(46),TDB47(47),
		TDB48(48),TDB49(49),TDB50(50),TDB51(51),TDB52(52),TDB53(53),TDB54(54),TDB55(55),
		TDB56(56),TDB57(57),TDB58(58),TDB59(59),TDB60(60),TDB61(61),TDB62(62),TDB63(63),
		TDB64(64),TDB65(65),TDB66(66),TDB67(67),TDB68(68),TDB69(69),TDB70(70),TDB71(71),
		TDB72(72),TDB73(73),TDB74(74),TDB75(75),TDB76(76),TDB77(77),TDB78(78),TDB79(79),
		TDB80(80),TDB81(81),TDB82(82),TDB83(83),TDB84(84),TDB85(85),TDB86(86),TDB87(87),
		TDB88(88),TDB89(89),TDB90(90),TDB91(91),TDB92(92),TDB93(93),TDB94(94),TDB95(95),
		TDB96(96),TDB97(97),TDB98(98),TDB99(99),TDB100(100),TDB101(101),TDB102(102),TDB103(103),
		TDB104(104),TDB105(105),TDB106(106),TDB107(107),TDB108(108),TDB109(109),TDB110(110),TDB111(111),
		TDB112(112),TDB113(113),TDB114(114),TDB115(115),TDB116(116),TDB117(117),TDB118(118),TDB119(119),
		TDB120(120),TDB121(121),TDB122(122),TDB123(123),TDB124(124),TDB125(125),TDB126(126),TDB127(127),
		TDB128(128),TDB129(129),TDB130(130),TDB131(131),TDB132(132),TDB133(133),TDB134(134),TDB135(135),
		TDB136(136),TDB137(137),TDB138(138),TDB139(139),TDB140(140),TDB141(141),TDB142(142),TDB143(143),
		TDB144(144),TDB145(145),TDB146(146),TDB147(147),TDB148(148),TDB149(149),TDB150(150),TDB151(151),
		TDB152(152),TDB153(153),TDB154(154),TDB155(155),TDB156(156),TDB157(157),TDB158(158),TDB159(159),
		TDB160(160),TDB161(161),TDB162(162),TDB163(163),TDB164(164),TDB165(165),TDB166(166),TDB167(167),
		TDB168(168),TDB169(169),TDB170(170),TDB171(171),TDB172(172),TDB173(173),TDB174(174),TDB175(175),
		TDB176(176),TDB177(177),TDB178(178),TDB179(179),TDB180(180),TDB181(181),TDB182(182),TDB183(183),
		TDB184(184),TDB185(185),TDB186(186),TDB187(187),TDB188(188),TDB189(189),TDB190(190),TDB191(191),
		TDB192(192),TDB193(193),TDB194(194),TDB195(195),TDB196(196),TDB197(197),TDB198(198),TDB199(199),
		TDB200(200),TDB201(201),TDB202(202),TDB203(203),TDB204(204),TDB205(205),TDB206(206),TDB207(207),
		TDB208(208),TDB209(209),TDB210(210),TDB211(211),TDB212(212),TDB213(213),TDB214(214),TDB215(215),
		TDB216(216),TDB217(217),TDB218(218),TDB219(219),TDB220(220),TDB221(221),TDB222(222),TDB223(223),
		TDB224(224),TDB225(225),TDB226(226),TDB227(227),TDB228(228),TDB229(229),TDB230(230),TDB231(231),
		TDB232(232),TDB233(233),TDB234(234),TDB235(235),TDB236(236),TDB237(237),TDB238(238),TDB239(239),
		TDB240(240),TDB241(241),TDB242(242),TDB243(243),TDB244(244),TDB245(245),TDB246(246),TDB247(247),
		TDB248(248),TDB249(249),TDB250(250),TDB251(251),TDB252(252),TDB253(253),TDB254(254),TDB255(255);
		
		private final int value;
		Material(int Value) 
		{
            value = Value;
        }
	}
	
	//currently all available material slots
	public static enum BiomeOres
	{
		Coast(240),
		SubTropicalDesert(240),
		SemiAridDesert(240),
		TemperateDesert(240),
		AridDesert(240),
		
		GrassSavanna(240),
		TreeSavanna(240),
		XericShrubland(240),
		DrySteppe(240),
		DesertSteppe(240),
		
		BroadLeafForest(240),
		DecidiousForest(240),
		Shrubland(240),
		ModerateSteppe(240),
		BarrenSteppe(240),
		
		SubTropicalRainForest(240),
		TemperateForest(240),
		Mediterranean(240),
		Tundra(240),
		AlpineTundra(240),
		
		TropicalRainForest(240),
		MonsoonForest(240),
		Taiga(240),
		MontaneForest(240),
		SnowPolarDesert(240);
		
//		Coast(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		SubTropicalDesert(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		SemiAridDesert(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		TemperateDesert(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		AridDesert(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		
//		GrassSavanna(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		TreeSavanna(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		XericShrubland(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		DrySteppe(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		DesertSteppe(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		
//		BroadLeafForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		DecidiousForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		Shrubland(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		ModerateSteppe(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		BarrenSteppe(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		
//		SubTropicalRainForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		TemperateForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		Mediterranean(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		Tundra(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		AlpineTundra(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		
//		TropicalRainForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		MonsoonForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		Taiga(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		MontaneForest(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
//		SnowPolarDesert(240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255);
		
		
        private final int[] value;

        BiomeOres(int... newValue) {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
	}
	//out of 10000
	public static enum BiomeOrePercentages
	{
		Coast(100),
		SubTropicalDesert(100),
		SemiAridDesert(100),
		TemperateDesert(100),
		AridDesert(100),
		
		GrassSavanna(100),
		TreeSavanna(100),
		XericShrubland(100),
		DrySteppe(100),
		DesertSteppe(100),
		
		BroadLeafForest(100),
		DecidiousForest(100),
		Shrubland(100),
		ModerateSteppe(100),
		BarrenSteppe(100),
		
		SubTropicalRainForest(100),
		TemperateForest(100),
		Mediterranean(100),
		Tundra(100),
		AlpineTundra(100),
		
		TropicalRainForest(100),
		MonsoonForest(100),
		Taiga(100),
		MontaneForest(100),
		SnowPolarDesert(100);
		
        private final int[] value;

        BiomeOrePercentages(int... newValue) {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
	}
	
	//stores min/max per 2 slots
	public static enum BiomeOreElevations
	{
		Coast(40,60),
		SubTropicalDesert(40,60),
		SemiAridDesert(40,60),
		TemperateDesert(40,60),
		AridDesert(40,60),
		
		GrassSavanna(40,60),
		TreeSavanna(40,60),
		XericShrubland(40,60),
		DrySteppe(40,60),
		DesertSteppe(40,60),
		
		BroadLeafForest(40,60),
		DecidiousForest(40,60),
		Shrubland(40,60),
		ModerateSteppe(40,60),
		BarrenSteppe(40,60),
		
		SubTropicalRainForest(40,60),
		TemperateForest(40,60),
		Mediterranean(40,60),
		Tundra(40,60),
		AlpineTundra(40,60),
		
		TropicalRainForest(40,60),
		MonsoonForest(40,60),
		Taiga(40,60),
		MontaneForest(40,60),
		SnowPolarDesert(40,60);
		
        private final int[] value;

        BiomeOreElevations(int... newValue) {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
	}
	
	//currently preferred slots for unique biome materials
	public static enum BiomeMaterials
	{
		Coast(0,1,2,16,17,18,32,33,34),
		SubTropicalDesert(3,4,5,19,20,21,35,36,37),
		SemiAridDesert(6,7,8,22,23,24,38,39,40),
		TemperateDesert(9,10,11,25,26,27,41,42,43),
		AridDesert(12,13,14,28,29,30,44,45,46),
		
		GrassSavanna(48,49,50,64,65,66,80,81,82),
		TreeSavanna(51,52,53,67,68,69,83,84,85),
		XericShrubland(54,55,56,70,71,72,86,87,88),
		DrySteppe(57,58,59,73,74,75,89,90,91),
		DesertSteppe(60,61,62,76,77,78,92,93,94),
		
		BroadLeafForest(96,97,98,112,113,114,128,129,130),
		DecidiousForest(99,100,101,115,116,117,131,132,133),
		Shrubland(102,103,104,118,119,120,134,135,136),
		ModerateSteppe(105,106,107,121,122,122,137,138,139),
		BarrenSteppe(108,109,110,123,124,125,140,141,142),
		
		SubTropicalRainForest(144,145,146,160,161,162,176,177,178),
		TemperateForest(147,148,149,163,164,165,179,180,181),
		Mediterranean(150,151,152,166,167,168,182,183,184),
		Tundra(153,154,155,169,170,171,185,186,187),
		AlpineTundra(156,157,158,172,173,174,188,189,190),
		
		TropicalRainForest(192,193,194,208,209,210,224,225,226),
		MonsoonForest(195,196,197,211,212,213,227,228,229),
		Taiga(198,199,200,214,215,216,230,231,232),
		MontaneForest(201,202,203,217,218,219,233,234,235),
		SnowPolarDesert(204,205,206,220,221,222,236,237,238);
		
        private final int[] value;

        BiomeMaterials(int... newValue) {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
	}
	
	//Biome graph location: minMoisture,minHeight,maxMoisture,maxHeight
	public static enum BiomePosition
	{
		Coast(0,0,20,20),
		SubTropicalDesert(0,20,20,40),
		SemiAridDesert(0,40,20,60),
		TemperateDesert(0,60,20,80),
		AridDesert(0,80,20,101),
		
		GrassSavanna(20,0,40,20),
		TreeSavanna(20,20,40,40),
		XericShrubland(20,40,40,60),
		DrySteppe(20,60,40,80),
		DesertSteppe(20,80,40,101),
		
		BroadLeafForest(40,0,60,20),
		DecidiousForest(40,20,60,40),
		Shrubland(40,40,60,60),
		ModerateSteppe(40,60,60,80),
		BarrenSteppe(40,80,60,101),
		
		SubTropicalRainForest(60,0,80,20),
		TemperateForest(60,20,80,40),
		Mediterranean(60,40,80,60),
		Tundra(60,60,80,80),
		AlpineTundra(60,80,80,101),
		
		TropicalRainForest(80,0,101,20),
		MonsoonForest(80,20,101,40),
		Taiga(80,40,101,60),
		MontaneForest(80,60,101,80),
		SnowPolarDesert(80,80,101,101);

        private final int[] value;

        BiomePosition(int... newValue) {
            value = newValue;
        }

        public int[] getValue() 
        { 
        	return value; 
        }
        
        public static String getBiomeNameByCoords(int x, int y)
        {
        	for(int i = 0; i < BiomePosition.values().length; i++)
        	{
        		int[] val = BiomePosition.values()[i].getValue();
        		if(x >= val[0] && y >= val[1] && x < val[2] && y < val[3])
        		{
        			return BiomePosition.values()[i].name();
        		}
        	}
			return null;
        }
    }
	
	
}
