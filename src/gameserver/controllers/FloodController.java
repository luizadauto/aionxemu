/**
 * This file is part of Aion X Emu <aionxemu.com>
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.controllers;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import gameserver.model.FloodIP;



/**
 * Class that controlls all flood
 * 
 * @author Divinity
 */
public class FloodController
{
    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
    private static final Logger		log	= Logger.getLogger(FloodController.class);

    private static ArrayList<FloodIP> IPs = new ArrayList<FloodIP>();

    public static void addIP(String IP)
    {
        IPs.add(new FloodIP(IP));
    }

    public static boolean checkFlood(String IP)
    {
        boolean found	= false;
        int 	i		= 0;
        
        while (!found && i<IPs.size())
        {
            if (IPs.get(i).getIP().equals(IP))
                found = true;
            else
                i++;
        }
        
        return IPs.get(i).checkFlood();
    }

    public static boolean exist(String IP)
    {
        for (FloodIP fIP : IPs)
            if (fIP.getIP().equals(IP))
                return true;
                
        return false;
    }

    public static int getIP(String ip)
    {
        boolean found	= false;
        int 	i		= 0;
        
        while (!found && i<IPs.size())
        {
            if (IPs.get(i).getIP().equals(ip))
                found = true;
            else
                i++;
        }
        
        return i;
    }

    public static void addConnection(String IP)
    {
        IPs.get(getIP(IP)).addConnection();
    }

    public static int getConnection(String IP)
    {
        return IPs.get(getIP(IP)).size();
    }
}
