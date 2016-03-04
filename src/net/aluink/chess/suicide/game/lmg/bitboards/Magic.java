package net.aluink.chess.suicide.game.lmg.bitboards;

public class Magic {
	
	public long mask;
	public int shift;
	public long magic;
	public long [] attSets;
	
	public static final Magic [] RMagic = new Magic[64];
	public static final Magic [] BMagic = new Magic[64];
	
	static private boolean initialized = false;
	
	public static void init(){
		if(!initialized){
			initBMagic();
			initRMagic();
			initialized = true;
		}
	}
	
	private Magic(){};
	
	static final int LEFT = -1;
	static final int RIGHT = 1;
	static final int UP = 8;
	static final int DOWN = -8;
	
	public static void main(String[] args) {
		Magic.init();
	}
	
	private static void initBMagic() {
		for(int i = 0;i < 64;i++){
			BMagic[i] = new Magic();
			BMagic[i].mask = BOccMasks[i];
			BMagic[i].shift = BShift[i];
			BMagic[i].magic = BMagicN[i];
			int attSetCount = 1 << BShift[i];
			
			BMagic[i].attSets = new long[attSetCount];
			for(int j = 0;j < attSetCount;j++){
				BMagic[i].attSets[j] = -1;
			}
			for(long j = 0;j < attSetCount;j++){
				long bb = 0L;
				int bit = i;
				while((bit / 8 > 1) && (bit % 8 < 6)) bit += DOWN + RIGHT;
				int s = (i - bit) / 7;
				long l = 0;
				for(;s != 0;s--,l++){
					bb |= (j >> l & 1L) << bit;
					bit += UP + LEFT;
				}
				if((bit / 8 < 6) && (bit % 8 > 1)){
					bit += UP + LEFT;
					for(;(bit / 8 < 7) && (bit % 8 > 0);l++){
						bb |= (j >> l & 1L) << bit;
						bit += UP + LEFT;
					}
				}
				bit = i;
				while((bit / 8 > 1) && (bit % 8 > 1)) bit += DOWN + LEFT;
				s = (i - bit) / 9;
				for(;s != 0;s--,l++){
					bb |= (j >> l & 1L) << bit;
					bit += UP + RIGHT;
				}
				if((bit / 8 < 6) && (bit % 8 < 6)){
					bit += UP + RIGHT;
					for(;(bit / 8 < 7) && (bit % 8 < 7);l++){
						long t = (j >> l & 1L) << bit;
						bb |= t;
						bit += UP + RIGHT;
					}
				}
				long attSet = 0L;
				bit = i;
				
				while((bit / 8 < 7) && (bit % 8 < 7)){
					bit += UP + RIGHT;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				bit = i;
				while((bit / 8 < 7) && (bit % 8 > 0)){
					bit += UP + LEFT;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				bit = i;
				while((bit / 8 > 0) && (bit % 8 < 7)){
					bit += DOWN + RIGHT;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				bit = i;
				while((bit / 8 > 0) && (bit % 8 > 0)){
					bit += DOWN + LEFT;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				
				BMagic[i].attSets[(int)((bb * BMagic[i].magic) >> (64-BMagic[i].shift) & ShiftMask[BMagic[i].shift])] = attSet;
				
			}
		}
	}

	private static void initRMagic() {
		for(int i = 0;i < 64;i++){
			RMagic[i] = new Magic();
			RMagic[i].mask = ROccMasks[i];
			RMagic[i].shift = RShift[i];
			RMagic[i].magic = RMagicN[i];
			
			RMagic[i].attSets = new long[1 << RShift[i]];
			int attSetCount = 1 << RShift[i];
			
			RMagic[i].attSets = new long[attSetCount];
			for(int j = 0;j < attSetCount;j++){
				RMagic[i].attSets[j] = -1;
			}
			for(long j = 0;j < attSetCount;j++){
				long bb = 0L;
				int bit = i;
				while((bit / 8 > 1)) bit += DOWN;
				int s = (i - bit) / 8;
				long l = 0;
				for(;s != 0;s--,l++){
					bb |= (j >> l & 1L) << bit;
					bit += UP;
				}
				if((bit / 8 < 6)){
					bit += UP;
					for(;(bit / 8 < 7);l++){
						bb |= (j >> l & 1L) << bit;
						bit += UP;
					}
				}
				bit = i;
				while((bit % 8 > 1)) bit += LEFT;
				s = i - bit;
				for(;s != 0;s--,l++){
					bb |= (j >> l & 1L) << bit;
					bit += RIGHT;
				}
				if((bit % 8 < 6)){
					bit += RIGHT;
					for(;(bit % 8 < 7);l++){
						long t = (j >> l & 1L) << bit;
						bb |= t;
						bit += RIGHT;
					}
				}
				long attSet = 0L;
				bit = i;

				while((bit / 8 < 7)){
					bit += UP;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				bit = i;
				while((bit % 8 > 0)){
					bit += LEFT;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				bit = i;
				while((bit % 8 < 7)){
					bit += RIGHT;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				bit = i;
				while((bit / 8 > 0)){
					bit += DOWN;
					attSet |= 1L << bit;
					if((bb >> bit & 1L) == 1)
						break;
				}
				RMagic[i].attSets[(int)((bb * RMagic[i].magic) >> (64-RMagic[i].shift) & ShiftMask[RMagic[i].shift])] = attSet;
			}
		}
	}

	public static final long [] ShiftMask = {
		0x0,0x1,0x3,0x7,0xF,0x1F,0x3F,0x7F,0xFF,
		0x1FF,0x3FF,0x7FF,0xFFF,0x1FFF,0x3FFF,0x7FFF,0xFFFF
	};
	
	static final int [] RShift = {
		12,11,11,11,11,11,11,12,
		11,10,10,10,10,10,10,11,
		11,10,10,10,10,10,10,11,
		11,10,10,10,10,10,10,11,
		11,10,10,10,10,10,10,11,
		11,10,10,10,10,10,10,11,
		11,10,10,10,10,10,10,11,
		12,11,11,11,11,11,11,12
	};
	
	static final int [] BShift = {
		6,5,5,5,5,5,5,6,
		5,5,5,5,5,5,5,5,
		5,5,7,7,7,7,5,5,
		5,5,7,9,9,7,5,5,
		5,5,7,9,9,7,5,5,
		5,5,7,7,7,7,5,5,
		5,5,5,5,5,5,5,5,
		6,5,5,5,5,5,5,6
	};
	
	static final long [] ROccMasks = {
		282578800148862L,
		565157600297596L,
		1130315200595066L,
		2260630401190006L,
		4521260802379886L,
		9042521604759646L,
		18085043209519166L,
		36170086419038334L,
		282578800180736L,
		565157600328704L,
		1130315200625152L,
		2260630401218048L,
		4521260802403840L,
		9042521604775424L,
		18085043209518592L,
		36170086419037696L,
		282578808340736L,
		565157608292864L,
		1130315208328192L,
		2260630408398848L,
		4521260808540160L,
		9042521608822784L,
		18085043209388032L,
		36170086418907136L,
		282580897300736L,
		565159647117824L,
		1130317180306432L,
		2260632246683648L,
		4521262379438080L,
		9042522644946944L,
		18085043175964672L,
		36170086385483776L,
		283115671060736L,
		565681586307584L,
		1130822006735872L,
		2261102847592448L,
		4521664529305600L,
		9042787892731904L,
		18085034619584512L,
		36170077829103616L,
		420017753620736L,
		699298018886144L,
		1260057572672512L,
		2381576680245248L,
		4624614895390720L,
		9110691325681664L,
		18082844186263552L,
		36167887395782656L,
		35466950888980736L,
		34905104758997504L,
		34344362452452352L,
		33222877839362048L,
		30979908613181440L,
		26493970160820224L,
		17522093256097792L,
		35607136465616896L,
		9079539427579068672L,
		8935706818303361536L,
		8792156787827803136L,
		8505056726876686336L,
		7930856604974452736L,
		6782456361169985536L,
		4485655873561051136L,
		9115426935197958144L,
		};

	static final long [] BOccMasks = {
		18049651735527936L,
		70506452091904L,
		275415828992L,
		1075975168L,
		38021120L,
		8657588224L,
		2216338399232L,
		567382630219776L,
		9024825867763712L,
		18049651735527424L,
		70506452221952L,
		275449643008L,
		9733406720L,
		2216342585344L,
		567382630203392L,
		1134765260406784L,
		4512412933816832L,
		9024825867633664L,
		18049651768822272L,
		70515108615168L,
		2491752130560L,
		567383701868544L,
		1134765256220672L,
		2269530512441344L,
		2256206450263040L,
		4512412900526080L,
		9024834391117824L,
		18051867805491712L,
		637888545440768L,
		1135039602493440L,
		2269529440784384L,
		4539058881568768L,
		1128098963916800L,
		2256197927833600L,
		4514594912477184L,
		9592139778506752L,
		19184279556981248L,
		2339762086609920L,
		4538784537380864L,
		9077569074761728L,
		562958610993152L,
		1125917221986304L,
		2814792987328512L,
		5629586008178688L,
		11259172008099840L,
		22518341868716544L,
		9007336962655232L,
		18014673925310464L,
		2216338399232L,
		4432676798464L,
		11064376819712L,
		22137335185408L,
		44272556441600L,
		87995357200384L,
		35253226045952L,
		70506452091904L,
		567382630219776L,
		1134765260406784L,
		2832480465846272L,
		5667157807464448L,
		11333774449049600L,
		22526811443298304L,
		9024825867763712L,
		18049651735527936L,
		};

	static final long [] RMagicN = {
			  0xa8002c000108020L,
			  0x6c00049b0002001L,
			  0x100200010090040L,
			  0x2480041000800801L,
			  0x280028004000800L,
			  0x900410008040022L,
			  0x280020001001080L,
			  0x2880002041000080L,
			  0xa000800080400034L,
			  0x4808020004000L,
			  0x2290802004801000L,
			  0x411000d00100020L,
			  0x402800800040080L,
			  0xb000401004208L,
			  0x2409000100040200L,
			  0x1002100004082L,
			  0x22878001e24000L,
			  0x1090810021004010L,
			  0x801030040200012L,
			  0x500808008001000L,
			  0xa08018014000880L,
			  0x8000808004000200L,
			  0x201008080010200L,
			  0x801020000441091L,
			  0x800080204005L,
			  0x1040200040100048L,
			  0x120200402082L,
			  0xd14880480100080L,
			  0x12040280080080L,
			  0x100040080020080L,
			  0x9020010080800200L,
			  0x813241200148449L,
			  0x491604001800080L,
			  0x100401000402001L,
			  0x4820010021001040L,
			  0x400402202000812L,
			  0x209009005000802L,
			  0x810800601800400L,
			  0x4301083214000150L,
			  0x204026458e001401L,
			  0x40204000808000L,
			  0x8001008040010020L,
			  0x8410820820420010L,
			  0x1003001000090020L,
			  0x804040008008080L,
			  0x12000810020004L,
			  0x1000100200040208L,
			  0x430000a044020001L,
			  0x280009023410300L,
			  0xe0100040002240L,
			  0x200100401700L,
			  0x2244100408008080L,
			  0x8000400801980L,
			  0x2000810040200L,
			  0x8010100228810400L,
			  0x2000009044210200L,
			  0x4080008040102101L,
			  0x40002080411d01L,
			  0x2005524060000901L,
			  0x502001008400422L,
			  0x489a000810200402L,
			  0x1004400080a13L,
			  0x4000011008020084L,
			  0x26002114058042L,
			};

	static final long [] BMagicN = {
			  0x89a1121896040240L,
			  0x2004844802002010L,
			  0x2068080051921000L,
			  0x62880a0220200808L,
			  0x4042004000000L,
			  0x100822020200011L,
			  0xc00444222012000aL,
			  0x28808801216001L,
			  0x400492088408100L,
			  0x201c401040c0084L,
			  0x840800910a0010L,
			  0x82080240060L,
			  0x2000840504006000L,
			  0x30010c4108405004L,
			  0x1008005410080802L,
			  0x8144042209100900L,
			  0x208081020014400L,
			  0x4800201208ca00L,
			  0xf18140408012008L,
			  0x1004002802102001L,
			  0x841000820080811L,
			  0x40200200a42008L,
			  0x800054042000L,
			  0x88010400410c9000L,
			  0x520040470104290L,
			  0x1004040051500081L,
			  0x2002081833080021L,
			  0x400c00c010142L,
			  0x941408200c002000L,
			  0x658810000806011L,
			  0x188071040440a00L,
			  0x4800404002011c00L,
			  0x104442040404200L,
			  0x511080202091021L,
			  0x4022401120400L,
			  0x80c0040400080120L,
			  0x8040010040820802L,
			  0x480810700020090L,
			  0x102008e00040242L,
			  0x809005202050100L,
			  0x8002024220104080L,
			  0x431008804142000L,
			  0x19001802081400L,
			  0x200014208040080L,
			  0x3308082008200100L,
			  0x41010500040c020L,
			  0x4012020c04210308L,
			  0x208220a202004080L,
			  0x111040120082000L,
			  0x6803040141280a00L,
			  0x2101004202410000L,
			  0x8200000041108022L,
			  0x21082088000L,
			  0x2410204010040L,
			  0x40100400809000L,
			  0x822088220820214L,
			  0x40808090012004L,
			  0x910224040218c9L,
			  0x402814422015008L,
			  0x90014004842410L,
			  0x1000042304105L,
			  0x10008830412a00L,
			  0x2520081090008908L,
			  0x40102000a0a60140L,
			};


}
