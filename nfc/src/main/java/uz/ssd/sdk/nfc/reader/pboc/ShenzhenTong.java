package uz.ssd.sdk.nfc.reader.pboc;


import uz.ssd.sdk.nfc.SPEC;

final class ShenzhenTong extends StandardPboc {

    @Override
    protected SPEC.APP getApplicationId() {
        return SPEC.APP.SHENZHENTONG;
    }
}
