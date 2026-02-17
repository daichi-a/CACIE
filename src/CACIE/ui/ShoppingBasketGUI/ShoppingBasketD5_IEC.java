package CACIE.ui.ShoppingBasketGUI;

public class ShoppingBasketD5_IEC extends ShoppingBasket_IEC {

	public ShoppingBasketD5_IEC() {
		// TODO Auto-generated constructor stub
	}
	
	public int getFitnessValue(int index) {
		// 各エリアごとに点数を返す
		int currentAreaID = iconArray[index].getAreaID();
		if (currentAreaID == 1)
			return 100;
		else if (currentAreaID == 2)
			return 75;
		else if (currentAreaID == 3)
			return 50;
		else if (currentAreaID == 4)//ゴミ箱エリア
			return 0;
		else
			return 25;//評価してないエリア
	}
	
}
