package util;

import util.enums.TransferStatus;

public record TransferResult(TransferStatus status, int excess, int transferredAmount) {

    @Override
    public String toString() {
        return "Status: " + this.status + ", Excess: " + this.excess + ", Transferred amount: " + this.transferredAmount;
    }
}
