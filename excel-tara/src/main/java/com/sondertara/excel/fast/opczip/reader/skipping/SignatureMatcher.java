package com.sondertara.excel.fast.opczip.reader.skipping;

class SignatureMatcher {
    final Signature signature;
    int index = 0;

    SignatureMatcher(Signature signature) {
        this.signature = signature;
    }

    public boolean matchNext(byte b) {
        if (signature.at(index) == b) {
            index++;
            if (index >= signature.length()) {
                reset();
                return true;
            }
        } else {
            reset();
        }
        return false;
    }

    private void reset() {
        index = 0;
    }

}
