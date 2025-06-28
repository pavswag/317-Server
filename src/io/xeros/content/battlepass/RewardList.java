package io.xeros.content.battlepass;

import io.xeros.model.items.GameItem;
import lombok.Getter;

@Getter
public class RewardList {
    @Getter
    public enum NormalRewardList {

        I1(new GameItem(696, 5)),
        I2(new GameItem(696, 5)),
        I3(new GameItem(696, 5)),
        I4(new GameItem(696, 5)),
        I5(new GameItem(696, 5)),
        I6(new GameItem(696, 10)),
        I7(new GameItem(696, 10)),
        I8(new GameItem(696, 10)),
        I9(new GameItem(696, 10)),

        I10(new GameItem(696, 10)),
        I11(new GameItem(20011, 1)),
        I12(new GameItem(20011, 1)),
        I13(new GameItem(20011, 1)),
        I14(new GameItem(20014, 1)),
        I15(new GameItem(20014, 1)),
        I16(new GameItem(20014, 1)),
        I17(new GameItem(21553, 1)),
        I18(new GameItem(21553, 1)),
        I19(new GameItem(20789, 1)),
        I20(new GameItem(20789, 1)),
        I21(new GameItem(21028, 1)),
        I22(new GameItem(21028, 1)),
        I23(new GameItem(11920, 1)),
        I24(new GameItem(11920, 1)),
        I25(new GameItem(22981, 1)),
        I26(new GameItem(22981, 1)),
        I27(new GameItem(26490, 1)),
        I28(new GameItem(26490, 1)),
        I29(new GameItem(26713, 1)),
        I30(new GameItem(26713, 1)),
        I31(new GameItem(26713, 1)),
        I32(new GameItem(26713, 1)),
        I33(new GameItem(26713, 1)),
        I34(new GameItem(26713, 1)),
        I35(new GameItem(26713, 1)),
        I36(new GameItem(19710, 1)),
        I37(new GameItem(19710, 1)),
        I39(new GameItem(10330, 1)),
        I40(new GameItem(10330, 1)),
        I41(new GameItem(10332, 1)),
        I42(new GameItem(10332, 1)),
        I43(new GameItem(10334, 1)),
        I44(new GameItem(10334, 1)),
        I45(new GameItem(10336, 1)),
        I46(new GameItem(10336, 1)),
        I47(new GameItem(10338, 1)),
        I48(new GameItem(10338, 1)),
        I49(new GameItem(10340, 1)),
        I50(new GameItem(10340, 1)),
        I51(new GameItem(10342, 1)),
        I52(new GameItem(10342, 1)),
        I53(new GameItem(10344, 1)),
        I54(new GameItem(10344, 1)),
        I55(new GameItem(10346, 1)),
        I56(new GameItem(10346, 1)),
        I57(new GameItem(10348, 1)),
        I58(new GameItem(10348, 1)),
        I59(new GameItem(10350, 1)),
        I60(new GameItem(10350, 1)),
        I61(new GameItem(10352, 1)),
        I62(new GameItem(10352, 1)),
        I63(new GameItem(12422, 1)),
        I64(new GameItem(12422, 1)),
        I65(new GameItem(12424, 1)),
        I66(new GameItem(12424, 1)),
        I67(new GameItem(12426, 1)),
        I68(new GameItem(12426, 1)),
        I69(new GameItem(12437, 1)),
        I70(new GameItem(12437, 1)),
        I71(new GameItem(12600, 1)),
        I72(new GameItem(12600, 1)),
        I73(new GameItem(23242, 1)),
        I74(new GameItem(23242, 1)),
        I75(new GameItem(23336, 1)),
        I76(new GameItem(23336, 1)),
        I77(new GameItem(23339, 1)),
        I78(new GameItem(23339, 1)),
        I79(new GameItem(23342, 1)),
        I80(new GameItem(23342, 1)),
        I81(new GameItem(23345, 1)),
        I82(new GameItem(23345, 1)),
        I83(new GameItem(26486, 1)),
        I84(new GameItem(26486, 1)),
        I85(new GameItem(26482, 1)),
        I86(new GameItem(26482, 1)),
        I87(new GameItem(6739, 1)),
        I88(new GameItem(6739, 1)),
        I89(new GameItem(21012, 1)), //dhcb
        I90(new GameItem(26486, 1)), //rune cbow or
        I91(new GameItem(26482, 1)), // whip or
        I92(new GameItem(22249, 1)), //anguish or
        I93(new GameItem(20366, 1)), //torture or
        I94(new GameItem(19720, 1)), //ocult or
        I95(new GameItem(12924, 1)), //blow pipe
        I96(new GameItem(12924, 1)),//
        I97(new GameItem(11834, 1)), //bandos tassets
        I98(new GameItem(11832, 1)), //bandos chest
        I99(new GameItem(24271, 1)), //faceguard
        I100(new GameItem(11773, 1)),// beserker i
        I101(new GameItem(11771, 1)),// archers i
        I102(new GameItem(11283, 1)),// dfs
        I103(new GameItem(22981, 1)),// ferocious gloves
        I104(new GameItem(21006, 1)),// kodai
        I105(new GameItem(20789, 1)),// row i2
        I106(new GameItem(11826, 1)),// arma helm
        I107(new GameItem(11828, 1)),// arma body
        I108(new GameItem(11830, 1)),// arma skirt
        I109(new GameItem(11285, 1)),// arcane spirit shield
        I110(new GameItem(12821, 1)),// spectral spirit shield
        I111(new GameItem(20790, 1)),// row i1
        I113(new GameItem(20790, 1)),// row i1
        I114(new GameItem(20790, 1)),// row i1
        I115(new GameItem(2949, 3)), //golden hammer,
        I116(new GameItem(11785, 1)),// arma c bow
        I117(new GameItem(12899, 1)),// swamp trident
        I118(new GameItem(12006, 1)),// tent whip
        I119(new GameItem(21003, 1)),// elder maul
        I120(new GameItem(21034, 1)),// Dex scroll
        I121(new GameItem(21079, 1)),// arcane scroll
        I122(new GameItem(12902, 1)),// toxic staff
        I123(new GameItem(21633, 1)),// wyvern shield
        I124(new GameItem(21950, 2500)),// onyx d bolts e
        I125(new GameItem(11212, 2500)),// dragon arrows
        I129(new GameItem(4205, 3)),// consecration seed
        I130(new GameItem(6112, 3)),// kelda seed
        I131(new GameItem(22881, 3)),// attas
        I132(new GameItem(20903, 3)),// noxifer
        I133(new GameItem(20909, 3)),// buchu
        I134(new GameItem(22869, 3)),// celastrus
        I135(new GameItem(20906, 3)),// golpar
        I136(new GameItem(22883, 3)),// iasor
        I137(new GameItem(22885, 3)),// kronos
        I138(new GameItem(22875, 1)),// hespori
        I139(new GameItem(13346, 1)),// umb
        I140(new GameItem(13346, 1)),
        I141(new GameItem(13346, 1)),
        I142(new GameItem(13346, 1)),
        I143(new GameItem(11770, 1)),// seers i
        I144(new GameItem(24422, 1)),
        I145(new GameItem(22324, 1)),
        I146(new GameItem(27624, 1)),
        I147(new GameItem(20368, 1)),
        I148(new GameItem(20370, 1)),
        I149(new GameItem(20372, 1)),
        I150(new GameItem(20374, 1)),
        I151(new GameItem(22613, 1)),
        I154(new GameItem(22622, 1)),
        I155(new GameItem(22619, 1)),
        I156(new GameItem(22638, 1)),
        I157(new GameItem(22641, 1)),
        I158(new GameItem(22644, 1)),
        I159(new GameItem(24517, 1)),
        I160(new GameItem(24514, 1)),
        I161(new GameItem(24511, 1)),
        I162(new GameItem(22647, 1)),
        ;
        private final GameItem gameItem;
        NormalRewardList(GameItem gameItem) {
            this.gameItem = gameItem;
        }


    }

    @Getter
    public enum UltraRewardList {

        I1(new GameItem(22325, 1)),//Scythe
        I2(new GameItem(20997, 1)),//Tbow
        I3(new GameItem(27226, 1)),//Masori Helm
        I4(new GameItem(27229, 1)),//Masori Body
        I5(new GameItem(27232, 1)),//Masori Legs
        I6(new GameItem(20787, 1)),//Row i4
        I7(new GameItem(13681, 1)),//Cruciferous Codex
        I8(new GameItem(26221, 1)),//Ancient Ceremonial Body
        I9(new GameItem(26223, 1)),//Ancient Ceremonial skirt
        I10(new GameItem(26225, 1)),//Ancient Ceremonial helm
        I12(new GameItem(2400, 10)),//Silverlight Keys
        I13(new GameItem(27285, 2)),//Eyes of corruptor
        I15(new GameItem(13346, 10)),//Ultra Mystery Box
        I16(new GameItem(25985, 1)),//Elidinis ward
        I17(new GameItem(10559, 1)),//Healer Icon
        I18(new GameItem(2403, 2)),//$10 scroll x2
        I19(new GameItem(7776, 3)),//2.5k Donor credits
        I20(new GameItem(13302, 10)),//10x Bank Keys
        ;

        private final GameItem gameItem;
        UltraRewardList(GameItem gameItem) {
            this.gameItem = gameItem;
        }
    }
}
