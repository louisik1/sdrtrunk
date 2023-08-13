/*
 * *****************************************************************************
 * Copyright (C) 2014-2023 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * ****************************************************************************
 */

package io.github.dsheirer.module.decode.dmr.message.data.lc.full.motorola;

import io.github.dsheirer.bits.CorrectedBinaryMessage;
import io.github.dsheirer.identifier.Identifier;
import io.github.dsheirer.module.decode.dmr.identifier.DMRTalkgroup;
import io.github.dsheirer.module.decode.dmr.message.data.lc.full.FullLCMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Motorola Encryption Parameters
 * <p>
 * Note: observed as FLC payload for a PI_HEADER slot type.
 * Note: observed on a possible Hytera (clone) system that was configured as IP Site Connect compatible.
 */
public class MotorolaEncryptionParameters extends FullLCMessage
{
    private static final int[] KEY_ID = new int[]{16, 17, 18, 19, 20, 21, 22, 23};
    private static final int[] INITIALIZATION_VECTOR = new int[]{24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55};
    private static final int[] ALGORITHM = new int[]{56, 57, 58, 59, 60, 61, 62, 63};
    private static final int[] DESTINATION_GROUP = new int[]{64, 65, 66, 67, 68, 69, 70, 71};
    //Reed Solomon FEC: 72-95

    private DMRTalkgroup mTalkgroup;
    private List<Identifier> mIdentifiers;

    /**
     * Constructs an instance.
     *
     * @param message for the link control payload
     */
    public MotorolaEncryptionParameters(CorrectedBinaryMessage message, long timestamp, int timeslot)
    {
        super(message, timestamp, timeslot);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if(!isValid())
        {
            sb.append("[CRC-ERROR] ");
        }

        if(isEncrypted())
        {
            sb.append(" *ENCRYPTED*");
        }

        if(isReservedBitSet())
        {
            sb.append(" *RESERVED-BIT*");
        }

        sb.append("FLC MOTOROLA ENCRYPTION PARAMETERS - ALGORITHM:").append(getAlgorithm());
        sb.append(" KEY:").append(getKeyId());
        sb.append(" IV?:").append(getInitializationVector());
        sb.append(" TALKGROUP:").append(getTalkgroup());
        sb.append(" MSG:").append(getMessage().toHexString());
        return sb.toString();
    }

    public DMRTalkgroup getTalkgroup()
    {
        if(mTalkgroup == null)
        {
            mTalkgroup = new DMRTalkgroup(getMessage().getInt(DESTINATION_GROUP));
        }

        return mTalkgroup;
    }

    public int getKeyId()
    {
        return getMessage().getInt(KEY_ID);
    }

    public String getAlgorithm()
    {
        int algorithm = getMessage().getInt(ALGORITHM);

        if(algorithm == 0)
        {
            return "EP/ARC4";
        }

        return "UNK(" + algorithm + ")";
    }

    public String getInitializationVector()
    {
        return getMessage().getHex(INITIALIZATION_VECTOR, 8);
    }

    @Override
    public List<Identifier> getIdentifiers()
    {
        if(mIdentifiers == null)
        {
            mIdentifiers = new ArrayList<>();
            mIdentifiers.add(getTalkgroup());
        }

        return mIdentifiers;
    }
}
